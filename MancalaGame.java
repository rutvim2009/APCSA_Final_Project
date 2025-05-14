import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MancalaGame extends JFrame {
 private int[] board = new int[14]; // 6 pits per side + 2 stores
 private JButton[] pitButtons = new JButton[14];
 private boolean playerOneTurn = true;

 public MancalaGame() {
 setTitle("Mancala Game");
 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 setSize(800, 200);
 setLayout(new GridLayout(2, 7));

 initializeBoard();

 for (int i = 13; i >= 7; i--) { // Player 2 side (top row)
 add(createPitButton(i));
 }

 for (int i = 0; i <= 6; i++) { // Player 1 side (bottom row)
 add(createPitButton(i));
 }

 updateBoard();
 setVisible(true);
 }

 private void initializeBoard() {
 for (int i = 0; i < 14; i++) {
 board[i] = (i == 6 || i == 13) ? 0 : 4; // Stores start at 0
 }
 }

 private JButton createPitButton(int index) {
 pitButtons[index] = new JButton();
 pitButtons[index].addActionListener(e -> handlePitClick(index));
 return pitButtons[index];
 }

 private void handlePitClick(int index) {
 // Check turn and valid move
 if ((playerOneTurn && index >= 0 && index <= 5) ||
 (!playerOneTurn && index >= 7 && index <= 12)) {

 int stones = board[index];
 if (stones == 0) return;

 board[index] = 0;
 int i = index;

 while (stones > 0) {
 i = (i + 1) % 14;

 if ((playerOneTurn && i == 13) || (!playerOneTurn && i == 6))
 continue; // Skip opponent's store

 board[i]++;
 stones--;
 }

 // Capture logic
 if (playerOneTurn && i >= 0 && i <= 5 && board[i] == 1 && board[12 - i] > 0) {
 board[6] += board[i] + board[12 - i];
 board[i] = board[12 - i] = 0;
 } else if (!playerOneTurn && i >= 7 && i <= 12 && board[i] == 1 && board[12 - i] > 0) {
 board[13] += board[i] + board[12 - i];
 board[i] = board[12 - i] = 0;
 }

 // Extra turn if ends in player's store
 if ((playerOneTurn && i == 6) || (!playerOneTurn && i == 13)) {
 // same player continues
 } else {
 playerOneTurn = !playerOneTurn;
 }

 checkGameOver();
 updateBoard();
 }
 }

 private void updateBoard() {
 for (int i = 0; i < 14; i++) {
 pitButtons[i].setText("" + board[i]);
 }
 }

 private void checkGameOver() {
 boolean player1Empty = true;
 boolean player2Empty = true;

 for (int i = 0; i <= 5; i++) {
 if (board[i] != 0) player1Empty = false;
 }
 for (int i = 7; i <= 12; i++) {
 if (board[i] != 0) player2Empty = false;
 }

 if (player1Empty || player2Empty) {
 for (int i = 0; i <= 5; i++) {
 board[6] += board[i];
 board[i] = 0;
 }
 for (int i = 7; i <= 12; i++) {
 board[13] += board[i];
 board[i] = 0;
 }
 updateBoard();

 String winner;
 if (board[6] > board[13]) {
 winner = "Player 1 Wins!";
 } else if (board[13] > board[6]) {
 winner = "Player 2 Wins!";
 } else {
 winner = "It's a tie!";
 }

 JOptionPane.showMessageDialog(this, winner);
 System.exit(0);
 }
 }

 public static void main(String[] args) {
 SwingUtilities.invokeLater(MancalaGame::new);
 }
}