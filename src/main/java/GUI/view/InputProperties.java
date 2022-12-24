package GUI.view;

import lombok.Getter;
import GUI.actions.SetProperties;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// Начальня форма запуска приложения, где выставляются параметры симуляции модели

@Getter
public class InputProperties {
  private final ArrayList<JTextField> propertiesConfirm = new ArrayList<>();
  private final int elementCount = 7;

  public void start() {
    JFrame currentFrame = new JFrame("Параметры симуляции");
    currentFrame.setVisible(true);
    currentFrame.setForeground(Color.black);
    currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    currentFrame.setSize(580, 580);
    currentFrame.setLocation(900, 250);
    currentFrame.setLayout(null);

    JLabel deviceCountLbl = new JLabel("Кол-во приборов:");
    deviceCountLbl.setForeground(Color.black);
    currentFrame.getContentPane().add(deviceCountLbl)
            .setBounds(240, 60, 200, 30);
    JLabel customersCountLbl = new JLabel("Кол-во заказчиков:");
    customersCountLbl.setForeground(Color.black);
    currentFrame.getContentPane().add(customersCountLbl)
            .setBounds(240, 120, 200, 30);
    JLabel simTimeLbl = new JLabel("Время симуляции:");
    simTimeLbl.setForeground(Color.black);
    currentFrame.getContentPane().add(simTimeLbl)
            .setBounds(240, 180, 200, 30);
    JLabel bufferCapacityLbl = new JLabel("Размер буфера:");
    bufferCapacityLbl.setForeground(Color.black);
    currentFrame.getContentPane().add(bufferCapacityLbl)
            .setBounds(240, 240, 200, 30);
    JLabel timeMinLbl = new JLabel("T_min обработки заказа:");
    timeMinLbl.setForeground(Color.black);
    currentFrame.getContentPane().add(timeMinLbl)
            .setBounds(220, 300, 200, 30);
    JLabel timeMaxLbl = new JLabel("T_max обработки заказа:");
    timeMaxLbl.setForeground(Color.black);
    currentFrame.getContentPane().add(timeMaxLbl)
            .setBounds(220, 360, 200, 30);
    JLabel flowIntensityLbl = new JLabel("Интенсивность потока λ:");
    flowIntensityLbl.setForeground(Color.black);
    currentFrame.getContentPane().add(flowIntensityLbl)
            .setBounds(220, 420, 200, 30);

    ArrayList<JTextField> properties = new ArrayList<>(this.elementCount);
    properties.add(new JTextField("", 10));
    currentFrame.getContentPane().add(properties.get(0))
            .setBounds(190, 90, 200, 30);
    properties.add(new JTextField("", 10));
    currentFrame.getContentPane().add(properties.get(1))
            .setBounds(190, 150, 200, 30);
    properties.add(new JTextField("", 10));
    currentFrame.getContentPane().add(properties.get(2))
            .setBounds(190, 210, 200, 30);
    properties.add(new JTextField("", 10));
    currentFrame.getContentPane().add(properties.get(3))
            .setBounds(190, 270, 200, 30);
    properties.add(new JTextField("", 10));
    currentFrame.getContentPane().add(properties.get(4))
            .setBounds(190, 330, 200, 30);
    properties.add(new JTextField("", 10));
    currentFrame.getContentPane().add(properties.get(5))
            .setBounds(190, 390, 200, 30);
    properties.add(new JTextField("", 10));
    currentFrame.getContentPane().add(properties.get(6))
            .setBounds(190, 450, 200, 30);

    for (int i = 0; i < this.elementCount; i++) {
      propertiesConfirm.add(properties.get(i));
    }

    final JLabel title = new JLabel("ПАРАМЕТРЫ СИМУЛЯЦИИ:");
    title.setForeground(Color.black);
    currentFrame.getContentPane().add(title).setBounds(220, 10, 200, 30);

    final JButton continueBtn = new JButton(new SetProperties(currentFrame, propertiesConfirm));
    continueBtn.setText("Подтвердить");
    currentFrame.getContentPane().add(continueBtn).setBounds(210, 500, 150, 30);

    currentFrame.revalidate();
  }
}