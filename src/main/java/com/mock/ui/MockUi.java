package com.mock.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowManagerDescriptor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import com.mock.trading.ui.UITick;

public class MockUi {

	private static ToolWindowManager toolWindowManager;
	MyDoggyToolWindowManager myDoggyToolWindowManager;
	public static UITick tickUi = new UITick();

	public MockUi() {
		setUp();
	}

	public static Dimension defaultSize() {
		return new Dimension(1200, 800);
	}

	protected void setUp() {
		initComponents();
		initToolWindowManager();
	}

	protected void initComponents() {
	}

	protected void initToolWindowManager() {
		try {
			this.myDoggyToolWindowManager = new MyDoggyToolWindowManager();
			toolWindowManager = this.myDoggyToolWindowManager;

			ToolWindowManagerDescriptor twDesc = toolWindowManager
					.getToolWindowManagerDescriptor();
			twDesc.setNumberingEnabled(false);

			// Made all tools available
			for (ToolWindow window : toolWindowManager.getToolWindows())
				window.setAvailable(true);

			initContentManager();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public MyDoggyToolWindowManager getManager() {
		return this.myDoggyToolWindowManager;
	}

	protected void initContentManager() {
		ContentManager contentManager = toolWindowManager.getContentManager();

		contentManager.addContent("Tick", "Tick", null, tickUi);
		contentManager.getContent("Tick").setSelected(true);

	}

	public static ToolWindowManager getToolWindowManager() {
		return toolWindowManager;
	}

	public static void main(String[] args) throws Exception {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// UIManager.setLookAndFeel(new
		// com.jgoodies.looks.plastic.Plastic3DLookAndFeel());
		// UIManager.setLookAndFeel(new
		// com.jgoodies.looks.windows.WindowsLookAndFeel());
		// UIManager.setLookAndFeel(new SeaGlassLookAndFeel());

		UIManager.setLookAndFeel(new NimbusLookAndFeel());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("Mock");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				MockUi poc = new MockUi();
				poc.getManager().setPreferredSize(new Dimension(1200, 800));
				frame.add(poc.getManager(), BorderLayout.CENTER);
				// frame.setLocationRelativeTo(null);
				frame.pack();
				frame.setVisible(true);
				// SwingUtil.setFullScreen(frame);
				SwingUtil.centrePositionOnScreen(frame);
			}
		});

	}
}
