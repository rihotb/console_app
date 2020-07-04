import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Console {
  static final String URL = "jdbc:mysql://localhost/console_java";
  static final String USERNAME = "root";
  static final String PASSWORD = "";

  public static void main(String[] args) {
    int i = 0;
    int num = 0;
    while (i == 0) {
      System.out.println("[Notes]機能");
      System.out.println("0.機能一覧");
      System.out.println("1.Noteの一覧");
      System.out.println("2.Noteの登録");
      System.out.println("3.Noteの詳細");
      System.out.println("4.Noteの更新");
      System.out.println("5.Noteの削除");
      System.out.println("6.システム終了");

      displayMessage("[Notes]機能番号を入力してください");
      Scanner scan = new Scanner(System.in);

      try {
        num = scan.nextInt();

        if (num <= 6 || num >= 0) {
          if (num == 1) {
            index();
          } else if (num == 2) {
            register();
          } else if (num == 3) {
            detail();
          } else if (num == 4) {
            update();
          } else if (num == 5) {
            delete();
          } else if (num == 6) {
            i += 1;
          } else {
            System.out.println("[Notes]存在しない機能番号です");
          }
        }

      } finally {
        scan.close();
      }
    }

  }

  public static void displayMessage(String message) {
    System.out.println(message);
    System.out.print(">");
  }

  public static void index() {

    String sql = "SELECT * FROM notes";

    try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        PreparedStatement pstmt = connection.prepareStatement(sql);) {

      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        System.out.println("ID:" + id);
        System.out.println("タイトル:" + title);
        if (description.length() > 15) {
          System.out.println("詳細:" + description.substring(0, 15) + "…");
        } else {
          System.out.println("詳細:" + description);
        }
        System.out.println("--------------------");
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void register() {
    int i = 0;
    String title = "";
    String description = "";

    displayMessage("[Notes]タイトルを入力してください");
    Scanner scan = new Scanner(System.in);
    title = scan.next();

    while (i == 0) {
      if (title.length() > 20) {
        System.out.println("タイトルの文字数の上限は20文字です");
        displayMessage("[Notes]タイトルを入力してください");
        title = scan.next();
      } else {
        i += 1;
      }
    }

    displayMessage("[Notes]詳細を入力してください");
    description = scan.next();

    while (i == 1) {

      if (description.length() > 500) {
        System.out.println("詳細の文字数の上限は500文字です");
        displayMessage("[Notes]詳細を入力してください");
        description = scan.next();
      } else {
        i += 1;
      }
    }

    String sql = "INSERT INTO notes(title, description) VALUES (?, ?)";

    try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        PreparedStatement pstmt = connection.prepareStatement(sql);) {

      pstmt.setString(1, title);
      pstmt.setString(2, description);

      pstmt.executeUpdate();
      System.out.println("登録完了しました");

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      scan.close();
    }

  }

  public static void detail() {
    int i = 0;
    displayMessage("[Notes]詳細表示する情報のIDを入力してください");
    Scanner scan = new Scanner(System.in);
    int id = scan.nextInt();

    String sql = "SELECT * FROM notes WHERE id = ?";

    try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        PreparedStatement pstmt = connection.prepareStatement(sql);) {

      pstmt.setInt(1, id);
      ResultSet rs = pstmt.executeQuery();

      boolean rsNext = rs.next();
      while (i == 0) {
        if (rsNext == false) {
          System.out.println("存在しないIDです");
          displayMessage("[Notes]詳細表示する情報のIDを入力してください");
          int retryId = scan.nextInt();
          pstmt.setInt(1, retryId);
          rs = pstmt.executeQuery();
          rsNext = rs.next();

        } else {
          int findId = rs.getInt("id");
          String title = rs.getString("title");
          String description = rs.getString("description");
          System.out.println("ID:" + findId);
          System.out.println("タイトル:" + title);
          System.out.println("詳細:" + description);
          i += 1;
        }
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      scan.close();
    }
  }

  public static void update() {
    int i = 0;

    displayMessage("[Notes]更新する情報のIDを入力してください");
    Scanner scan = new Scanner(System.in);
    int id = scan.nextInt();

    String sqlSelect = "SELECT * FROM notes WHERE id = ?";
    String sqlUpdate = "UPDATE notes SET title = ?, description = ? WHERE id = ?";

    try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        PreparedStatement pstmt = connection.prepareStatement(sqlSelect);) {

      pstmt.setInt(1, id);

      ResultSet rs = pstmt.executeQuery();

      boolean rsNext = rs.next();

      while (i == 0) {
        if (rsNext == false) {
          System.out.println("存在しないIDです");
          displayMessage("[Notes]詳細表示する情報のIDを入力してください");
          id = scan.nextInt();
          pstmt.setInt(1, id);
          rs = pstmt.executeQuery();
          rsNext = rs.next();

        } else {
          String title = rs.getString("title");
          String description = rs.getString("description");

          System.out.println("[Notes]新しいタイトルを入力してください");
          System.out.println("[Notes]現在のタイトル:" + title);
          System.out.print(">");
          String updateTitle = scan.next();
          if (updateTitle.equals("")) {
            updateTitle = title;
          }

          System.out.println("[Notes]新しい詳細を入力してください");
          System.out.println("[Notes]現在の詳細:" + description);
          System.out.print(">");
          String updateDescription = scan.next();
          if (updateDescription.equals("")) {
            updateDescription = description;
          }

          PreparedStatement pstmtUpdate = connection.prepareStatement(sqlUpdate);

          pstmtUpdate.setInt(3, id);
          pstmtUpdate.setString(1, updateTitle);
          pstmtUpdate.setString(2, updateDescription);

          pstmtUpdate.executeUpdate();
          System.out.println("[Notes]ID:" + id + "を更新しました");

          i += 1;
        }
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      scan.close();
    }
  }

  public static void delete() {
    int i = 0;

    displayMessage("[Notes]削除する情報のIDを入力してください");
    Scanner scan = new Scanner(System.in);
    int id = scan.nextInt();

    String sql = "DELETE FROM notes WHERE id = ?";

    try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        PreparedStatement pstmt = connection.prepareStatement(sql);) {

      pstmt.setInt(1, id);

      int executeUpdate = pstmt.executeUpdate();

      while (i == 0) {
        if (executeUpdate == 0) {
          System.out.println("[Notes]存在しないIDです");
          displayMessage("[Notes]削除する情報のIDを入力してください");
          id = scan.nextInt();

        } else {
          System.out.println("[Notes]ID:" + id + "を削除しました");
          i += 1;
        }
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      scan.close();
    }
  }

}
