package program;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Order {
    private final int clientId;
    private final @NotNull String orderId;
    private final double startTime;

    public Order(int clientId, @NotNull String orderId, double startTime) {
        this.clientId = clientId;
        this.orderId = orderId;
        this.startTime = startTime;
    }

    public int clientId() {
        return clientId;
    }

    public @NotNull String orderId() {
        return orderId;
    }

    public double startTime() {
        return startTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Order that = (Order) obj;
        return this.clientId == that.clientId &&
                Objects.equals(this.orderId, that.orderId) &&
                Double.doubleToLongBits(this.startTime) == Double.doubleToLongBits(that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, orderId, startTime);
    }

    @Override
    public String toString() {
        return "Order[" +
                "clientId=" + clientId + ", " +
                "orderId=" + orderId + ", " +
                "startTime=" + startTime + ']';
    }
}