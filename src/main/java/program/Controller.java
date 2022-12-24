package program;

import lombok.Getter;
import program.components.*;
import program.events.Event;
import program.events.Type;
import statistics.Statistics;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Controller {
  public static Statistics statisticsPub = Statistics.getInstance();
  private final double workTime = Statistics.workTime;
  private Event currentEvent;
  private double currentTime;

  private final Statistics statistics;
  private final Buffer buffer;
  private final Terminal terminal;
  private final Distributer distributer;
  private final ArrayList<Client> clients;
  private final ArrayList<Device> devices;
  private ArrayList<Event> events;

  public Controller() {
    statisticsPub = Statistics.getInstance();
    currentTime = 0;
    buffer = new Buffer(statisticsPub.getBufferSize());
    devices = new ArrayList<>(statisticsPub.getDevicesCount());
    for (int i = 0; i < statisticsPub.getDevicesCount(); i++) {
      devices.add(new Device(i, statisticsPub));
    }
    distributer = new Distributer(buffer, devices, statisticsPub);
    clients = new ArrayList<>(statisticsPub.getClientsCount());
    for (int i = 0; i < statisticsPub.getClientsCount(); i++) {
      clients.add(new Client(i));
    }
    terminal = new Terminal(buffer, clients, statisticsPub);
    statistics = statisticsPub;
    initEvents();
  }

  public void stepMode() {
    currentEvent = events.remove(0);
    final Type currentType = currentEvent.eventType;
    final int currentId = currentEvent.id;
    currentTime = currentEvent.eventTime;
    if (currentType == Type.GENERATED) {
      List<Event> newEvents = terminal.putOrderToBuffer(currentId, currentTime);
      if (!events.isEmpty()) {
        events.addAll(newEvents);
        events.sort(Event::compareByTime);
      }
    } else if (currentType == Type.IN_PROGRESS) {
      final Event newEvent = distributer.sendOrderToDevice(currentTime, currentEvent);
      if (newEvent != null) {
        events.add(newEvent);
        events.sort(Event::compareByTime);
      }
    } else if (currentType == Type.COMPLETED) {
      devices.get(currentId).release(currentTime);
      events.add(new Event(Type.IN_PROGRESS, currentTime, null));
      events.sort(Event::compareByTime);
    }
  }

  public void auto() {
    while (!events.isEmpty()) {
      stepMode();
    }
  }

  private void initEvents() {
    events = new ArrayList<>();
    for (int i = 0; i < statisticsPub.getClientsCount(); i++) {
      events.add(new Event(Type.GENERATED, clients.get(i).getNextOrderGenerationTime(), (i + "-" + 0), i));
    }
    if (events.size() > 0) {
      events.sort(Event::compareByTime);
    }
  }
}
