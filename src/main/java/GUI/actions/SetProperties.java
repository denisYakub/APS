package GUI.actions;

import GUI.Waveform;
import GUI.view.StepMode;
import program.Controller;
import statistics.Statistics;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

// Нажатие на кнопку "Подтвердить" в форме установки параметров симуляции

public class SetProperties extends AbstractAction {
  private final ArrayList<JTextField> inputFields;
  @NotNull
  private final JFrame prevFrame;

  public SetProperties(@NotNull final JFrame prevFrame, final ArrayList<JTextField> array) {
    this.prevFrame = prevFrame;
    this.inputFields = array;
  }

  @Override
  public void actionPerformed(@NotNull final ActionEvent e) {
    Statistics.countOfDevices = Integer.parseInt(inputFields.get(0).getText());
    Statistics.countOfClients = Integer.parseInt(inputFields.get(1).getText());
    Statistics.workTime = Integer.parseInt(inputFields.get(2).getText());
    Statistics.sizeOfBuffer = Integer.parseInt(inputFields.get(3).getText());
    Statistics.minimum = Double.parseDouble(inputFields.get(4).getText());
    Statistics.maximum = Double.parseDouble(inputFields.get(5).getText());
    Statistics.lambda = Double.parseDouble(inputFields.get(6).getText());
    createStepModeFrame();
  }

  private void createStepModeFrame() {
    prevFrame.setState(JFrame.ICONIFIED);
    final Controller controller = new Controller();
    final Waveform waveform = new Waveform(controller);
    final StepMode newFrame = new StepMode(controller, waveform);
    newFrame.start();
  }
}

