package GUI.view;

import program.Controller;
import statistics.ClientStatistics;

import javax.swing.*;
import java.awt.*;


public class ResultsTable {
  final Controller controller;
  final JFrame prevFrame;

  public ResultsTable(final Controller controller, final JFrame frame) {
    this.controller = controller;
    this.prevFrame = frame;
  }

  public void start() {
    JFrame currentFrame = new JFrame() {
    };
    currentFrame.setVisible(true);
    currentFrame.setTitle("Сводная таблица");
    currentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    currentFrame.setSize(new Dimension(1500, 500));
    currentFrame.setLocation(300, 350);
    String[] columnNames = {"Id заказчика","Кол-во заказов", "% отказа", "T_avg в программе",
      "T_avg ожидания", "T_avg обслуживания", "Дисперсия T_wait", "Дисперсия Т_service", "Прибор", "Эффективность прибора"};

    String[][] data = new String[Controller.statisticsPub.getClientsCount()][10];
    int i = 0;
    for (ClientStatistics clientStat : controller.getStatistics().getClientsStats()) {
      data[i][0] = String.valueOf(controller.getClients().get(i).getClientId());
      data[i][1] = String.valueOf(clientStat.getGeneratedOrdersCount());
      data[i][2] = String.valueOf((double) clientStat.getCanceledOrdersCount() / clientStat.getGeneratedOrdersCount());
      data[i][3] = String.valueOf(clientStat.getTotalTime() / clientStat.getGeneratedOrdersCount());
      System.out.println(clientStat.getTotalOnBufferTime() / clientStat.getGeneratedOrdersCount());
      data[i][4] = String.valueOf(clientStat.getTotalOnBufferTime() / clientStat.getGeneratedOrdersCount());
      data[i][5] = String.valueOf(clientStat.getTotalOnDeviceTime() / clientStat.getGeneratedOrdersCount());
      data[i][6] = String.valueOf(clientStat.getOnBufferDispersion());
      data[i][7] = String.valueOf(clientStat.getOnDeviceDispersion());
      if (i < Controller.statisticsPub.getDevicesCount()) {
        data[i][8] = String.valueOf(i);
        data[i][9] = String.valueOf(
                Controller.statisticsPub.getDevicesWorkTime().get(i) / controller.getCurrentTime());
      }
      i++;
    }
    JTable table = new JTable(data, columnNames);
    JScrollPane scroll = new JScrollPane(table);
    table.setPreferredScrollableViewportSize(new Dimension(1500, 500));
    currentFrame.getContentPane().add(scroll);
    currentFrame.revalidate();
  }
}
