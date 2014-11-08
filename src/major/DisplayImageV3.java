/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package major;

/**
 *
 * @author hitesh
 */
import java.awt.CardLayout;  
import java.awt.Dimension;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.awt.image.BufferedImage;  
import java.io.IOException;  
import java.net.MalformedURLException;  
import java.net.URL;  
  
import javax.imageio.ImageIO;  
import javax.swing.ImageIcon;  
import javax.swing.JButton;  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JPanel;  
import javax.swing.JComponent;  
  
public class DisplayImageV3  
{  
  private static final String IMAGE_ADDR = "http://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/" +  
        "Schnepfenfliege_Rhagio_scolopaceus2.jpg/250px-Schnepfenfliege_Rhagio_scolopaceus2.jpg";  
  private static final Dimension MAIN_SIZE = new Dimension(300, 300);  
  private static final String BUTTON_PANEL = "Button Panel";  
  private static final String IMAGE_PANEL = "Image Panel";  
  private static final String IMAGE_PATH = "1.jpg";  
  private JPanel mainPanel = new JPanel();  
  private CardLayout cardlayout = new CardLayout();  
  
  public DisplayImageV3()  
  {  
    mainPanel.setLayout(cardlayout);  
    mainPanel.setPreferredSize(MAIN_SIZE);  
    mainPanel.add(createBtnPanel(), BUTTON_PANEL);  
    mainPanel.add(createImagePanel(), IMAGE_PANEL);  
  }  
  
  private JPanel createBtnPanel()  
  {  
    JButton startBtn = new JButton("Start");  
    startBtn.addActionListener(new StartBtnListener());  
      
    JPanel buttonPanel = new JPanel();  
    buttonPanel.add(startBtn);  
    return buttonPanel;  
  }  
  
  private JPanel createImagePanel()  
  {  
    BufferedImage image = null;  
    try  
    {  
      image = ImageIO.read(new URL(IMAGE_ADDR));  
    }  
    catch (MalformedURLException e)  
    {  
      e.printStackTrace();  
    }  
    catch (IOException e)  
    {  
      e.printStackTrace();  
    }  
    JPanel imagePanel = new JPanel();  
      
    if (image != null)  
    {  
      imagePanel.add(new JLabel(new ImageIcon(image)));  
    }  
    return imagePanel;  
  }  
  
  public JComponent getComponent()  
  {  
    return mainPanel;  
  }  
    
  private class StartBtnListener implements ActionListener  
  {  
    public void actionPerformed(ActionEvent e)  
    {  
      cardlayout.show(mainPanel, IMAGE_PANEL);  
    }  
  }  
  
  private static void createAndShowUI()  
  {  
    JFrame frame = new JFrame("Display Image");  
    frame.getContentPane().add(new DisplayImageV3().getComponent());  
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
    frame.pack();  
    frame.setLocationRelativeTo(null);  
    frame.setVisible(true);  
  }  
  
  public static void main(String[] args)  
  {  
    java.awt.EventQueue.invokeLater(new Runnable()  
    {  
      public void run()  
      {  
        createAndShowUI();  
      }  
    });  
  }  
}  