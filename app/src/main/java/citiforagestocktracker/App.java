
package citi;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class App {

  // The URL for the API
  private static final String YAHOO_FINANCE_API =
      "https://finance.yahoo.com/quote/";

  public static void main(String[] args) {

    // Stock symbol for the Dow Jones Industrial Average
    String symbol = "^DJI";
    // Wait time in milliseconds between queries
    int waitTimeMs = 5000;
    // Queue for containing timestamps and stock price
    Queue<ArrayList<Object>> stockDataQueue = new LinkedList<>();

    // This is the loop for querying data
    while (true) {

      // Try to query the stock information
      try {
        Stock stock = YahooFinance.get(symbol);
        // Get the current stock price
        BigDecimal price = stock.getQuote().getPrice();
        // Record the timestamp for the query
        Date timestamp = new Date();

        // Add the stockData to the queue, in the form (timestamp, price)
        ArrayList<Object> stockData = new ArrayList<Object>();
        stockData.add(timestamp);
        stockData.add(price);
        stockDataQueue.add(stockData);

        // Print the stockData
        System.out.println(stockData);

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
}
