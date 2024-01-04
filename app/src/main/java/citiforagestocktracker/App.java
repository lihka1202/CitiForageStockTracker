
package citi;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class App extends Application {
  // The URL for the API
  private static final String YAHOO_FINANCE_API =
      "https://finance.yahoo.com/quote/";
  // Stock symbol for the Dow Jones Industrial Average
  private String symbol = "^DJI";
  // Wait time in milliseconds between queries
  private int waitTimeMs = 1000;

  private LineChart<Number, Number> lineChart;
  private XYChart.Series<Number, Number> series;

  @Override
  public void start(Stage stage) {
    stage.setTitle("Dow Jones Industrial Average");
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Time (seconds)");
    yAxis.setLabel("Stock Price (USD)");
    lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle("Dow Jones Industrial Average Stock Price");
    series = new XYChart.Series<>();
    // series.setName("Line");
    lineChart.getData().add(series);
    Scene scene = new Scene(lineChart, 800, 600);
    stage.setScene(scene);
    stage.show();

    // Start a background thread for querying and updating the chart data
    new Thread(this::updateData).start();
  }

  private void updateData() {
    // Queue for containing timestamps and stock price
    Queue<ArrayList<Object>> stockDataQueue = new LinkedList<>();
    long startSeconds = Instant.now().getEpochSecond();

    // This is the loop for querying data
    while (true) {

      // Try to query the stock information
      try {
        Stock stock = YahooFinance.get(symbol);
        // Get the current stock price
        BigDecimal price = stock.getQuote().getPrice();
        // Record the timestamp for the query
        Date timestamp = new Date();
        long currSeconds = Instant.now().getEpochSecond();

        // Add the stockData to the queue, in the form (timestamp, price)
        ArrayList<Object> stockData = new ArrayList<Object>();
        stockData.add(timestamp);
        stockData.add(price);
        stockDataQueue.add(stockData);

        // Print the stockData
        System.out.println(stockData);

        // Update the chart data
        long secSinceStart = currSeconds - startSeconds;
        series.getData().add(new XYChart.Data(secSinceStart, price));

        // Update the chart on the JavaFX Application Thread
        lineChart.getData().retainAll();
        lineChart.getData().add(series);
      }

      // Catch exception if there is a connection error
      catch (IOException e) {
        System.out.println("Failure to connect. Trying again.");
      }

      // Wait before repeating the query
      try {
        Thread.sleep(waitTimeMs);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) { launch(args); }
}
