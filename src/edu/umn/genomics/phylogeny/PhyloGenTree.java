package edu.umn.genomics.phylogeny;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import edu.umn.genomics.component.SaveImage;
import edu.umn.genomics.component.SavePDF;
import edu.umn.genomics.table.ExceptionHandler;

public class PhyloGenTree extends JPanel {  
  // file menu // load, save, image, pdf
  // edit font, background, foreground, select font, select color
  // view menu leaflist, jtree, phylodiagram, phenogram, cladogram, eurogram, curvogram
  // view toolbar leaflist, jtree, phylodiagram, phenogram, cladogram, eurogram, curvogram
  class NamedNode {
    String name;
    DefaultMutableTreeNode node;
    public NamedNode(String name, DefaultMutableTreeNode node) {
      this.name = name;
      this.node = node;
    }
    public DefaultMutableTreeNode getNode() {
      return node;
    }
    public String toString() {
      return name;
    }
  }
  DefaultListModel treeListModel = new DefaultListModel();
  JList trees = new JList(treeListModel);
  JMenuBar menubar = null;
  JFileChooser fc = new JFileChooser();
  String lastURL = "";
  JTextField path;
  ListSelectionListener lsl = new ListSelectionListener() {
    public void valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
        NamedNode nn = (NamedNode)trees.getSelectedValue();
        showTree(nn.getNode(), nn.toString());
      }
    }
  };

  public PhyloGenTree() {
    setLayout(new BorderLayout());
    trees.addListSelectionListener(lsl);
    JScrollPane jsp = new JScrollPane(trees);
    add(jsp);
  }

  private JMenuBar getJMenuBar() {
    if (menubar != null) 
      return menubar;
    JMenuBar mb = new JMenuBar();
    JMenuItem mi;
    JMenu fileMenu = new JMenu("File");
      mi = (JMenuItem)fileMenu.add(new JMenuItem("load tree file"));
      mi.setMnemonic('f');
      mi.getAccessibleContext().setAccessibleDescription("load tree file");
      mi.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            int retVal = fc.showOpenDialog(((JComponent)e.getSource()).getTopLevelAncestor());
            if (retVal == JFileChooser.APPROVE_OPTION) {
              try {
                loadTree(fc.getSelectedFile().getCanonicalPath());
              } catch (IOException ioex) {
                  ExceptionHandler.popupException(""+ioex);
              }
            }
          }
        });
      mi = (JMenuItem)fileMenu.add(new JMenuItem("load tree from URL"));
      mi.setMnemonic('u');
      mi.getAccessibleContext().setAccessibleDescription("load tree from URL");
      mi.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              String path = JOptionPane.showInputDialog(
                ((JComponent)e.getSource()).getTopLevelAncestor(),
                "Enter the URL or File location of a Phylogenic tree that you would like to load:", 
                "Load a phylogenic Tree.", 
                JOptionPane.QUESTION_MESSAGE);
              if (path != null) {
                lastURL = path;
                loadTree(path);
              }
            } catch (Exception ex) {
                ExceptionHandler.popupException(""+ex);
            }
          }
        });
      mi = (JMenuItem)fileMenu.add(new JMenuItem("quit"));
      mi.setMnemonic('q');
      mi.getAccessibleContext().setAccessibleDescription("quit");
      mi.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            System.exit(0);
          }
        });

    mb.add(fileMenu);
    menubar = mb;
    return menubar;
  }
  
  public static BufferedReader getBufferedReader(String source) throws IOException {
    // URL?
    try {
      URL url = new URL(source);
      try {
        InputStream is = url.openStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        return in;
      } catch (Exception se) {
        ExceptionHandler.popupException(""+se);
      }
    }  catch (Exception ue) {
      ExceptionHandler.popupException(""+ue);
    }
    // local file?
    try {
      BufferedReader in = new BufferedReader(new FileReader(source));
      //System.err.println("local file: " + source);
      return in;
    } catch (IOException e) {
      if (source.charAt(0) == '~') {
        try {
          String args[] = new String[3];
          args[0] = "/usr/bin/csh";
          args[1] = "-c";
          args[2] = "echo " + source;
          Process p = Runtime.getRuntime().exec(args);
          BufferedReader br = new BufferedReader(
                                new InputStreamReader(p.getInputStream()));
          String path = "";
          String so;
          while ((so = br.readLine()) != null) {
            path += so;
          }
          BufferedReader in = new BufferedReader(new FileReader(path));
          //System.err.println("local file: " + path);
          return in;
        } catch (Exception rte) {
          ExceptionHandler.popupException(""+rte);
        }
      }
      //System.err.println(e);
      throw e;
    }
  }
  
  public List loadTree(String source) {
    List trees = null;
    try {
      BufferedReader rdr = getBufferedReader(source);
      NewickReader nw = new NewickReader();
      nw.readtokens(rdr);
      trees = nw.getTrees();
      for (int i = 0; i < nw.getTrees().size();i++) {
        DefaultMutableTreeNode tn = (DefaultMutableTreeNode)nw.getTrees().get(i);
        String title = (source + ((nw.getTrees().size() > 1) ? "#" + (i+1) : ""));
        treeListModel.addElement(new NamedNode(title, tn)); 
      }
    } catch (Exception ex) {
      ExceptionHandler.popupException(""+ex);
    }
    return trees;
  }

  public static void setViewToolBar(JFrame frame, Component view, JComponent extraButtons[])
 {
    final Component comp = view;
    final JFrame theframe = frame;
    JButton jBtn;
    JToolBar jtb = new JToolBar();
    // Close
    jBtn = new JButton("Close");
    jBtn.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          theframe.dispose();
        }
      }
    );
    jBtn.setToolTipText("Close this view");
    jtb.add(jBtn);
    if (view != null) {
      // Save Image
      jBtn = new JButton("Save Image");
      if (System.getProperty("java.specification.version").compareTo("1.4")>=0) {
        jBtn.setToolTipText("Save this view as an image");
        jBtn.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              try {
                SaveImage.saveImage(comp);
              } catch (IOException ioex) {
                ExceptionHandler.popupException(""+ioex);
              }
            }
          }
        );
      } else {
        jBtn.setToolTipText("Save Image requires Java 1.4");
        jBtn.setEnabled(false);
      }
      jtb.add(jBtn);

      // Save PDF
      jBtn = new JButton("Save PDF");
      if (System.getProperty("java.specification.version").compareTo("1.3")>=0) {
        jBtn.setToolTipText("Save this view as an image");
        jBtn.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              try {
                SavePDF.savePDF(comp);
              } catch (IOException ioex) {
                ExceptionHandler.popupException(""+ioex);
              }
            }
          }
        );
      } else {
        jBtn.setToolTipText("Save Image requires Java 1.4");
        jBtn.setEnabled(false);
      }
      jtb.add(jBtn);

    }
    if (extraButtons != null) {
      for (int i = 0; i < extraButtons.length; i++) {
        jtb.add(extraButtons[i]);
      }
    }
    theframe.getContentPane().add( jtb, BorderLayout.NORTH);
  }

  public JFrame showTree(TreeNode root, String title) {
    PhyloTree pt = new PhyloTree();
    pt.setTreeModel(new DefaultTreeModel(root));
    JFrame jpf = new JFrame(title);
    jpf.getContentPane().add(pt);
    jpf.setSize(400,400);
    jpf.setLocation(100, 100);
    jpf.setVisible(true);
    setViewToolBar(jpf, pt.getGraph().getGraphDisplay(), null);
    return jpf;
  }

  public JFrame showJTree(TreeNode root, String title) {
    JTree jt = new JTree(root);
    JScrollPane jsp = new JScrollPane(jt);
    JFrame jtf = new JFrame(title);
    jtf.getContentPane().add(jsp);
    jtf.setSize(400,400);
    jtf.setLocation(100, 100);
    jtf.setVisible(true);
    setViewToolBar(jtf, jt, null);
    return jtf;
  }

  public static void main(String[] args) {
    System.err.println("Build date:  Tue Jan 28 13:37:00 CST 2003");
    PhyloGenTree pgt = new PhyloGenTree();
    JFrame jf = new JFrame("PhyloGenTree");
    jf.getContentPane().add(pgt);
    jf.setJMenuBar(pgt.getJMenuBar());
    jf.setSize(400,400);
    jf.setLocation(20, 20);
    jf.setVisible(true);
    for (int i = 0; i < args.length; i++) {
      List treeList = pgt.loadTree(args[i]);
      for (int j = 0; treeList != null && j < treeList.size(); j++) {
        pgt.showTree((TreeNode)treeList.get(j), 
                     args[i] + (treeList.size()>1 ? "#" + i : ""));
        // pgt.showJTree((TreeNode)treeList.get(j), 
        //              args[i] + (treeList.size()>1 ? "#" + i : ""));
      }
    }
  }
}
