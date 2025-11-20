package src.service;

import src.db.Database;
import src.factory.Factory;
import srck.model.Transaction;
import src.strategy.TransactionStrategy;
import src.visitor.AuditVisitor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class AsyncTransactionProcessor {
    private final BlockingQueue<TransactionRequest> queue = new LinkedBlockingQueue<>();
    private final List<Consumer<String>> listeners = new CopyOnWriteArrayList<>();
    private final AuditVisitor auditor = new AuditVisitor();

    public AsyncTransactionProcessor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(this::processQueue);
    }

    public void addObserver(Consumer<String> listener) {
        listeners.add(listener);
    }

    public void submit(TransactionRequest request) {
        queue.offer(request);
        notify("Транзакция " + request.getId() + " добавлена в очередь.");
    }

    public double getAuditTotal() {
        return auditor.getTotalVolume();
    }

    private void processQueue() {
        while (true) {
            try {
                TransactionRequest request = queue.take();
                auditor.visit(request);
                processAtomically(request);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processAtomically(TransactionRequest request) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Начало ACID транзакции

            try {
                TransactionStrategy strategy = TransactionFactory.getStrategy(request.getType());
                strategy.execute(request, conn);

                conn.commit();
                notify("УСПЕХ: Транзакция " + request.getId() + " выполнена.");
            } catch (Exception e) {
                conn.rollback();
                notify("ОШИБКА: Транзакция " + request.getId() + " отклонена. " + e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            notify("Критическая ошибка БД.");
        }
    }

    private void notify(String message) {
        for (Consumer<String> listener : listeners) {
            listener.accept(message);
        }
    }
}