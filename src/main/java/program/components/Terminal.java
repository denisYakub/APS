package program.components;

import program.Order;
import program.events.Event;
import program.events.Type;
import statistics.Statistics;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Terminal {
  @NotNull
  private final Buffer buffer;
  @NotNull
  private final List<Client> clients;
  @NotNull
  private final Statistics statistics;

  public Terminal(@NotNull final Buffer buffer,
                  @NotNull final List<Client> clients,
                  @NotNull final Statistics statistics) {
    this.buffer = buffer;
    this.clients = clients;
    this.statistics = statistics;
  }

  private Order receiveOrder(final int currentId, final double currentTime) {
    return clients.get(currentId).generateOrder(currentTime);
  }

  public List<Event> putOrderToBuffer(final int currentId, final double currentTime) {
    final Order currentOrder = receiveOrder(currentId, currentTime);
    buffer.addOrder(currentOrder);
    List<Event> events = new ArrayList<>();
    events.add(new Event(Type.IN_PROGRESS, currentTime, currentOrder.orderId()));
    final double nextOrderTime = currentTime + clients.get(currentId).getNextOrderGenerationTime();
    if (nextOrderTime < Statistics.workTime) {
      events.add(new Event(Type.GENERATED, nextOrderTime, null, currentId));
    }
    statistics.orderGenerated(currentId);
    return events;
  }
}
