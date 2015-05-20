/*
Definitive Guide to Swing for Java 2, Second Edition
By John Zukowski     
ISBN: 1-893115-78-X
Publisher: APress
*/

package Main;

import com.jidesoft.swing.CheckBoxTree;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;

public class CheckBoxNodeTreeSample {
	
	public static void main(String args[]) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JButton button = new JButton("print");
	  
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		DefaultMutableTreeNode mercury = new DefaultMutableTreeNode("Mercury");
		root.add(mercury);
		DefaultMutableTreeNode venus = new DefaultMutableTreeNode("Venus");
		root.add(venus);
		DefaultMutableTreeNode mars = new DefaultMutableTreeNode("Mars");
		root.add(mars);
		DefaultMutableTreeNode uranus = new DefaultMutableTreeNode("uranus");
		root.add(uranus);
		DefaultMutableTreeNode jupiter = new DefaultMutableTreeNode("jupiter");
		root.add(jupiter);
		DefaultMutableTreeNode saturn = new DefaultMutableTreeNode("saturn");
		root.add(saturn);
		CheckBoxTree x = new CheckBoxTree(root);
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 300, 150);
		panel.add(x);
		
		JScrollPane scrollPane = new JScrollPane();
		
		frame.add(scrollPane);
		scrollPane.add(panel);
		frame.add(button, BorderLayout.SOUTH);
		frame.setSize(301, 200);
		frame.setVisible(true);
	}
}