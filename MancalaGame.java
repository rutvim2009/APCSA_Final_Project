import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;

public class MancalaGame extends JFrame {
    private Clip clip;
    private int[] board = new int[14]; // 6 pits per side + 2 stores
    private boolean playerOneTurn = true;
    private BoardPanel boardPanel;
    private boolean animating = false;

    public MancalaGame() {
        setTitle("Mancala Game - Vertical with Animation & Sound");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    
        int pitHeight = 80;
        int spacing = 10;
        int totalPitsHeight = 6 * pitHeight + 5 * spacing;
        int storeHeight = pitHeight;
    
        int verticalPaddingTop = 40;    // Extra space above top store & label
        int verticalPaddingBottom = 40; // Extra space below bottom store & label
    
        setSize(300, totalPitsHeight + 2 * storeHeight + 2 * spacing + verticalPaddingTop + verticalPaddingBottom);
    
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
    
        try {
            File soundFile = new File("/Users/RutviM/Downloads/newproj.wav"); // Update path if needed
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        initializeBoard();
        boardPanel = new BoardPanel();
        add(boardPanel);
        setVisible(true);
    }
    

    private void initializeBoard() {
        for (int i = 0; i < 14; i++) {
            board[i] = (i == 6 || i == 13) ? 0 : 4;
        }
    }

    private class BoardPanel extends JPanel {
        private Rectangle[] pitBounds = new Rectangle[14];

        public BoardPanel() {
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (animating) return;

                    Point click = e.getPoint();
                    for (int i = 0; i < pitBounds.length; i++) {
                        if (pitBounds[i] != null && pitBounds[i].contains(click)) {
                            handlePitClick(i);
                            break;
                        }
                    }
                }
            });
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setBackground(new Color(222, 184, 135)); // Wood-like background
        
            int width = getWidth();
            int height = getHeight();
            int pitWidth = 80;
            int pitHeight = 80;
            int spacing = 10;
        
            int verticalPaddingTop = 40;
            int verticalPaddingBottom = 40;
        
            int shiftUp = 20;  // amount to shift up everything but Player 1's store
        
            // Player 2 store (top), shifted UP by shiftUp
            pitBounds[13] = new Rectangle(width / 2 - pitWidth / 2, 10 + verticalPaddingTop - shiftUp, pitWidth, pitHeight);
        
            // Player 1 store (bottom), NO shift (stay put)
            pitBounds[6] = new Rectangle(width / 2 - pitWidth / 2, height - pitHeight - 10 - verticalPaddingBottom, pitWidth, pitHeight);
        
            g2.setColor(new Color(139, 69, 19)); // Store color
            g2.fillRect(pitBounds[13].x, pitBounds[13].y, pitBounds[13].width, pitBounds[13].height);
            g2.fillRect(pitBounds[6].x, pitBounds[6].y, pitBounds[6].width, pitBounds[6].height);
        
            drawPebbles(g2, 13, pitBounds[13]);
            drawPebbles(g2, 6, pitBounds[6]);
        
            // Draw pits between stores, shifted UP by shiftUp
            int startY = pitBounds[13].y + pitHeight + spacing;
        
            for (int row = 0; row < 6; row++) {
                int y = startY + row * (pitHeight + spacing);
        
                // Left column: Player 2 pits (12 to 7)
                int p2Index = 12 - row;
                pitBounds[p2Index] = new Rectangle(width / 2 - pitWidth - spacing, y, pitWidth, pitHeight);
                g2.fillOval(pitBounds[p2Index].x, pitBounds[p2Index].y, pitWidth, pitHeight);
                drawPebbles(g2, p2Index, pitBounds[p2Index]);
        
                // Right column: Player 1 pits (0 to 5)
                int p1Index = row;
                pitBounds[p1Index] = new Rectangle(width / 2 + spacing, y, pitWidth, pitHeight);
                g2.fillOval(pitBounds[p1Index].x, pitBounds[p1Index].y, pitWidth, pitHeight);
                drawPebbles(g2, p1Index, pitBounds[p1Index]);
            }
        
            // Draw player labels as before (no changes needed)
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        
            String label2 = "Player 2";
            String label1 = "Player 1";
        
            FontMetrics fm = g2.getFontMetrics();
        
            int label2Width = fm.stringWidth(label2);
            int label1Width = fm.stringWidth(label1);
        
            // Player 2 label (above top store)
            int label2X = pitBounds[13].x + (pitBounds[13].width - label2Width) / 2;
            int label2Y = pitBounds[13].y - 10; // Slightly above top store
        
            // Player 1 label (below bottom store)
            int label1X = pitBounds[6].x + (pitBounds[6].width - label1Width) / 2;
            int label1Y = pitBounds[6].y + pitBounds[6].height + 25; // Below bottom store
        
            // Highlight active player
            if (playerOneTurn) {
                g2.setColor(Color.RED);
                g2.drawString(label1, label1X, label1Y);
                g2.setColor(Color.BLACK);
                g2.drawString(label2, label2X, label2Y);
            } else {
                g2.setColor(Color.RED);
                g2.drawString(label2, label2X, label2Y);
                g2.setColor(Color.BLACK);
                g2.drawString(label1, label1X, label1Y);
            }
        }
        
              

        private void drawPebbles(Graphics2D g2, int pitIndex, Rectangle pit) {
            g2.setColor(Color.WHITE);
            int stones = board[pitIndex];
            for (int s = 0; s < stones; s++) {
                int px = pit.x + 10 + (s % 4) * 15;
                int py = pit.y + 10 + (s / 4) * 15;
                g2.fillOval(px, py, 10, 10);
            }
            g2.setColor(Color.BLACK);
            g2.drawString("" + board[pitIndex], pit.x + pit.width / 2 - 5, pit.y + pit.height + 12);
        }
    }

    private void handlePitClick(int index) {
        if ((playerOneTurn && index >= 0 && index <= 5) || (!playerOneTurn && index >= 7 && index <= 12)) {
            int stones = board[index];
            if (stones == 0 || animating) return;

            board[index] = 0;
            animateMove(index, stones);
        }
    }

    private void animateMove(int startIndex, int stones) {
        animating = true;
        Timer timer = new Timer(300, null);

        final int[] currentIndex = {startIndex};
        final int[] remainingStones = {stones};

        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentIndex[0] = (currentIndex[0] + 1) % 14;

                if ((playerOneTurn && currentIndex[0] == 13) || (!playerOneTurn && currentIndex[0] == 6)) {
                    return; // Skip opponent's store
                }

                clip.setFramePosition(0);
                clip.start();
                board[currentIndex[0]]++;
                remainingStones[0]--;

                boardPanel.repaint();

                if (remainingStones[0] == 0) {
                    timer.stop();

                    int last = currentIndex[0];

                    // Capture logic
                    if (playerOneTurn && last >= 0 && last <= 5 && board[last] == 1 && board[12 - last] > 0) {
                        board[6] += board[last] + board[12 - last];
                        board[last] = board[12 - last] = 0;
                    } else if (!playerOneTurn && last >= 7 && last <= 12 && board[last] == 1 && board[12 - last] > 0) {
                        board[13] += board[last] + board[12 - last];
                        board[last] = board[12 - last] = 0;
                    }

                    // Continue move if last pit is on player's side and not empty
                    if (board[last] > 1 &&
                            ((playerOneTurn && last >= 0 && last <= 5) || (!playerOneTurn && last >= 7 && last <= 12))) {
                        int nextStones = board[last];
                        board[last] = 0;
                        animateMove(last, nextStones);
                        return;
                    } else {
                        playerOneTurn = !playerOneTurn;
                    }

                    animating = false;
                    checkGameOver();
                    boardPanel.repaint();
                }
            }
        });

        timer.start();
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

            boardPanel.repaint();

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
