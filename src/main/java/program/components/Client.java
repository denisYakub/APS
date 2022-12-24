package program.components;

import lombok.Getter;
import program.Order;
import statistics.Statistics;

import java.util.Random;

import static statistics.Statistics.lambda;

@Getter
public class Client {
  final private int clientId;
  private int ordersCount;

  public Client(final int clientId) {
    this.clientId = clientId;
    this.ordersCount = 0;
  }

  public double getNextOrderGenerationTime() {
    return (-1.0 / lambda) * Math.log(Math.random());
  }


  public Order generateOrder(final double currentTime) {
    return new Order(clientId, String.valueOf(clientId) + '-' + ordersCount++, currentTime);
  }
}
