package GUI.actions;

import GUI.view.ResultsTable;
import program.Controller;

import javax.swing.*;
import java.awt.event.ActionEvent;

// Нажатиие на кнопку "Сводная таблица"

public class AutoMode extends AbstractAction {
  private final Controller controller;
  final private JFrame prevFrame;


  public AutoMode(final Controller controller,
                  final JFrame frame) {
    this.controller = controller;
    this.prevFrame = frame;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    controller.auto();
    ResultsTable modeFrame = new ResultsTable(controller, prevFrame);
    modeFrame.start();
  }
}
