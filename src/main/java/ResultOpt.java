import program.Controller;
import statistics.Statistics;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ResultOpt {
    private final List<String[]> resultRow = new ArrayList<>();
    private Double maxFail = 1.0;
    private Double minWorkTime = 1.0;

    // Оптимальноя конфигурация
    public void main2() {
        for (int countOfDevice = 1; countOfDevice < 11; countOfDevice++) {
            for (int countOfSizeBuffer = 1; countOfSizeBuffer < 11; countOfSizeBuffer++) {
                for (double time = 0; time < 4; time++) {
                    Statistics.countOfClients = 20; // всегда 15
                    Statistics.workTime = 1000; // всегда 1000 у.е.
                    Statistics.countOfDevices = countOfDevice;
                    Statistics.sizeOfBuffer = countOfSizeBuffer;
                    Statistics.minimum = time * 0.1;
                    Statistics.maximum = (time + 1) * 0.1;
                    Statistics.lambda = 2; // всегда 1, потом посмотрим
                    if (findResults()){
                        double price = 0;
                        price += countOfSizeBuffer * 2000;
                        switch ((int) time) {
                            case (0) : price += countOfDevice * 840000;
                            case (1) : price += countOfDevice * 440000;
                            case (2) : price += countOfDevice * 200000;
                            case (3) : price += countOfDevice * 110000;
                        }
                        this.resultRow.add(new String[]{String.valueOf(this.maxFail), String.valueOf(this.minWorkTime),
                                String.valueOf(time * 0.1), String.valueOf((time + 1) * 0.1),
                                String.valueOf(countOfDevice), String.valueOf(countOfSizeBuffer),
                                String.valueOf(price)});
                        System.out.println("Min time = " + time * 0.1 + ", Max time = " + (time + 1) * 0.1);
                        System.out.println("Devices count = " + countOfDevice);
                        System.out.println("Buffer size = " + countOfSizeBuffer);
                        System.out.println("Price = "+ price);
                        System.out.println("----------------------------------------");
                    }
                }
            }
        }
        JFrame currentFrame = new JFrame() {
        };
        currentFrame.setVisible(true);
        currentFrame.setTitle("Сводная таблица");
        currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        currentFrame.setSize(new Dimension(1500, 500));
        currentFrame.setLocation(300, 350);
        String[] columnNames = {"Max fail","Min work time in %", "Min time", "Max time",
                "Devices count", "Buffer size", "Price"};

        String[][] data = new String[resultRow.size()][10];
        int i = 0;
        for (String[] str : resultRow) {
            data[i][0] = str[0];
            data[i][1] = str[1];
            data[i][2] = str[2];
            data[i][3] = str[3];
            data[i][4] = str[4];
            data[i][5] = str[5];
            data[i][6] = str[6];
            i++;
        }
        JTable table = new JTable(data, columnNames);
        JScrollPane scroll = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(500, 900));
        currentFrame.getContentPane().add(scroll);
        currentFrame.revalidate();
    }

    public boolean findResults(){
        Controller controller = new Controller();
        controller.auto();
        // Кол-во отменённых заказов
        int sum = 0;
        for (int i = 0; i < controller.getStatistics().getClientsCount(); i++) {
            sum += controller.getStatistics().getClientsStats().get(i).getCanceledOrdersCount();
        }
        // Минимальное время обработки заказа в %
        double workTime = 1.1;
        for (int i = 0; i < controller.getStatistics().getDevicesCount(); i++){
            double newWorkTime = controller.getStatistics().getDevicesWorkTime().get(i) / controller.getCurrentTime();
            if (workTime > newWorkTime) {
                workTime = newWorkTime;
            }
        }
        // % отменённых заказов
        double maxFail = (double) sum / controller.getStatistics().getTotalOrdersCount();
        if (maxFail <= 0.1 && workTime >= 0.9){
            this.maxFail = maxFail;
            this.minWorkTime = workTime;
            System.out.println("true, maxFail = " + maxFail + ", minWorkTime in % of worker = " + workTime);
            return true;
        }
        return false;
    }
}
