package fatbeats.lib;

import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;

import javax.swing.*;

public class RecentFilesMenu extends JMenu {

	private static final long serialVersionUID = 1021920037451406458L;
	private int addedItemsCounter;
	private int threshold;
	private LinkedList<JMenuItem> recentItems;

	//standard JMenu constructors with a default value for the threshold
	public RecentFilesMenu() {
		super();
		recentItems = new LinkedList<JMenuItem>();
		threshold = 5;
	}

	public RecentFilesMenu(Action a) {
		super(a);
		recentItems = new LinkedList<JMenuItem>();
		threshold = 5;
	}

	public RecentFilesMenu(String s) {
		super(s);
		recentItems = new LinkedList<JMenuItem>();
		threshold = 5;
	}

	public RecentFilesMenu(String s, boolean b) {
		super(s, b);
		recentItems = new LinkedList<JMenuItem>();
		threshold = 5;
	}

	//new, class-specific constructors
	protected RecentFilesMenu(int itemsThreshold) {
		super();
		recentItems = new LinkedList<JMenuItem>();
		threshold = itemsThreshold;
	}

	protected RecentFilesMenu(Action a, int itemsThreshold) {
		super(a);
		recentItems = new LinkedList<JMenuItem>();
		threshold = itemsThreshold;
	}

	protected RecentFilesMenu(String s, int itemsThreshold) {
		super(s);
		recentItems = new LinkedList<JMenuItem>();
		threshold = itemsThreshold;
	}

	protected RecentFilesMenu(String s, boolean b, int itemsThreshold) {
		super(s, b);
		recentItems = new LinkedList<JMenuItem>();
		threshold = itemsThreshold;
	}

	//methods
	public static void paintMenuItems(Component[] items, Color paint) {
		for (Component c : items) {
			if (c instanceof JMenuItem) {
				JMenuItem item = (JMenuItem) c;
				item.setBackground(paint);
			}
		}
	}

	protected void highlightRecent(JMenuItem recentItem) {
		for (JMenuItem rfmi : recentItems) {
			rfmi.setBackground(Color.WHITE);
		}
		recentItem.setBackground(Color.LIGHT_GRAY);
	}

	protected JMenuItem[] getRecentItemsArray() {
		Object[] tmpItems = recentItems.toArray();
		JMenuItem[] items = new JMenuItem[tmpItems.length];
		for (int i = 0; i < items.length; i++) {
			items[i] = (JMenuItem) tmpItems[i];
		}
		return items;
	}

	protected void addRecentItem(Action recentItemAction) {
		JMenuItem itemToAdd = null;

		if (addedItemsCounter < threshold) {
			if (addedItemsCounter == 0) {
				this.add(new JSeparator(), this.getItemCount() - 2);
			}
			itemToAdd = new JMenuItem(recentItemAction);
			this.add(itemToAdd, this.getItemCount() - 2 - addedItemsCounter);
			recentItems.addFirst(itemToAdd);
			nameItemsAndPaint();
		} else {
			itemToAdd = new JMenuItem(recentItemAction);
			this.add(itemToAdd, this.getItemCount() - 2 - threshold);
			this.remove(this.getItemCount() - 3);
			recentItems.addFirst(itemToAdd);
			recentItems.removeLast();
			nameItemsAndPaint();
		}

		addedItemsCounter++;
	}

	private void nameItemsAndPaint() {
		for (int i = 0; i < recentItems.size(); i++) {
			String name = (String) recentItems.get(i).getAction().getValue(AbstractAction.DEFAULT);
			String fullName = i + 1 + " \"" + name + "\"";
			recentItems.get(i).setText(fullName);
			if (0 != i) {
				recentItems.get(i).setBackground(Color.WHITE);
			}
		}
	}
}
