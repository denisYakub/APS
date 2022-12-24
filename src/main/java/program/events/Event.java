package program.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {
  public Type eventType;
  public double eventTime;
  public String orderId;
  public int id = -1;

  public Event(final Type type, final double time, final String orderId, final int id) {
    this.eventType = type;
    this.eventTime = time;
    this.orderId = orderId;
    this.id = id;
  }

  public Event(final Type type, final double time, final String orderId) {
    this.eventType = type;
    this.eventTime = time;
    this.orderId = orderId;
  }

  public static int compareByTime(final Event l, final Event r) {
    if (l.eventTime < r.eventTime) {
      return -1;
    } else if (l.eventTime > r.eventTime) {
      return 1;
    } else if (l.eventType.ordinal() > r.eventType.ordinal()) {
      return -1;
    }
    return 0;
  }
}
