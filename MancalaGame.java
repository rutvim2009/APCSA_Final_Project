import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;

public class MancalaGame extends JFrame {
    private Clip clip;
    private int[] board = new int[14]; 
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

    
        setSize(300, totalPitsHeight + 2 * storeHeight + 2 * spacing + 80);
    
        setLocationRelativeTo(null); 
        setResizable(false);
    
        try {
            File soundFile = new File("/Users/RutviM/Downloads/newproj.wav"); 
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
            setBackground(new Color(222, 184, 135));
        
            int width = getWidth();
            int height = getHeight();
            int pitWidth = 80;
            int pitHeight = 80;
            int spacing = 10;
        
            int shiftUp = 20;
        
            // player 2 store
            pitBounds[13] = new Rectangle(width / 2 - pitWidth / 2, 50 - shiftUp, pitWidth, pitHeight);
        
            // player 1 store
            pitBounds[6] = new Rectangle(width / 2 - pitWidth / 2, height - pitHeight - 50, pitWidth, pitHeight);
        
            g2.setColor(new Color(139, 69, 19)); 
            g2.fillRect(pitBounds[13].x, pitBounds[13].y, pitBounds[13].width, pitBounds[13].height);
            g2.fillRect(pitBounds[6].x, pitBounds[6].y, pitBounds[6].width, pitBounds[6].height);
        
            drawPebbles(g2, 13, pitBounds[13]);
            drawPebbles(g2, 6, pitBounds[6]);
        
            
            int startY = pitBounds[13].y + pitHeight + spacing;
        
            for (int row = 0; row < 6; row++) {
                int y = startY + row * (pitHeight + spacing);
        
                // left column pits
                int p2Index = 12 - row;
                pitBounds[p2Index] = new Rectangle(width / 2 - pitWidth - spacing, y, pitWidth, pitHeight);
                g2.fillOval(pitBounds[p2Index].x, pitBounds[p2Index].y, pitWidth, pitHeight);
                drawPebbles(g2, p2Index, pitBounds[p2Index]);
        
                // right column pits
                int p1Index = row;
                pitBounds[p1Index] = new Rectangle(width / 2 + spacing, y, pitWidth, pitHeight);
                g2.fillOval(pitBounds[p1Index].x, pitBounds[p1Index].y, pitWidth, pitHeight);
                drawPebbles(g2, p1Index, pitBounds[p1Index]);
            }
        
            
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        
            String label2 = "Player 2";
            String label1 = "Player 1";
        
            FontMetrics fm = g2.getFontMetrics();
        
            int label2Width = fm.stringWidth(label2);
            int label1Width = fm.stringWidth(label1);
        
            
            int label2X = pitBounds[13].x + (pitBounds[13].width - label2Width) / 2;
            int label2Y = pitBounds[13].y - 10; 
        
            
            int label1X = pitBounds[6].x + (pitBounds[6].width - label1Width) / 2;
            int label1Y = pitBounds[6].y + pitBounds[6].height + 25; 
        
            // make active player label red
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
        if (index != 6 && index != 13 && board[index] > 0 && !animating) {
            int stones = board[index];
            board[index] = 0;
            animateMove(index, stones);
        }
        
    }

    private void animateMove(int startIndex, int stones) {
        animating = true;
    
        final int[] currentIndex = {startIndex};
        final int[] remainingStones = {stones};
    
        Timer timer = new Timer(300, null);
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do {
                    currentIndex[0] = (currentIndex[0] + 1) % 14;
                  // skip the other player's store
                } while ((playerOneTurn && currentIndex[0] == 13) || (!playerOneTurn && currentIndex[0] == 6));
    
                board[currentIndex[0]]++;
                remainingStones[0]--;
    
                // Play sound
                if (clip != null) {
                    clip.setFramePosition(0);
                    clip.start();
                }
    
                boardPanel.repaint();
    
                if (remainingStones[0] == 0) {
                    timer.stop();
                    animating = false;
                    playerOneTurn = !playerOneTurn; // switch plaayer turn
                    checkGameOver();
                }
            }
        });
    
        timer.start();
    }
    
    
    
    

    private void checkGameOver() {
        boolean allPitsEmpty = true;
    
        for (int i = 0; i <= 5; i++) {
            if (board[i] != 0) {
                allPitsEmpty = false;
                break;
            }
        }
        for (int i = 7; i <= 12; i++) {
            if (board[i] != 0) {
                allPitsEmpty = false;
                break;
            }
        }
    
        if (allPitsEmpty) {
            boardPanel.repaint();
    
            String winner = "";
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
