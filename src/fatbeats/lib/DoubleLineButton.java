package fatbeats.lib;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;

public class DoubleLineButton extends JButton {

	private static final long serialVersionUID = 8708008860152381909L;

	private JLabel upperLabel, bottomLabel;
	private boolean doubleTextUsed;

	public DoubleLineButton() {
		super();
	}

	public DoubleLineButton(Action a) {
		super(a);
	}

	public DoubleLineButton(Icon icon) {
		super(icon);
	}

	public DoubleLineButton(String text) {
		super(text);
	}

	public DoubleLineButton(String text, Icon icon) {
		super(text, icon);
	}

	public DoubleLineButton(String upperLine, String bottomLine) {
		super();
		prepareLabels(upperLine, bottomLine);
		doubleTextUsed = true;
	}

	public DoubleLineButton(String upperLine, String bottomLine, int textAlignment) {
		super();
		prepareLabels(upperLine, bottomLine);
		switch (textAlignment) {
			case SwingConstants.LEFT:
				adjustToLeft();
				break;
			case SwingConstants.CENTER:
				adjustToCenter();
				break;
			case SwingConstants.RIGHT:
				adjustToRigt();
				break;
			default:
				adjustToCenter();
		}
		doubleTextUsed = true;
	}

	public void setDoubleText(String upperLine, String bottomLine) {
		if (!doubleTextUsed) {
			this.setText("");
			prepareLabels(upperLine, bottomLine);
			doubleTextUsed = true;
		} else {
			upperLabel.setText(upperLine);
			bottomLabel.setText(bottomLine);
		}
	}

	public void alignDoubleTextLeft() {
		if (doubleTextUsed) {
			adjustToLeft();
		}
	}

	public void alignDoubleTextCenter() {
		if (doubleTextUsed) {
			adjustToCenter();
		}
	}

	public void alignDoubleTextRight() {
		if (doubleTextUsed) {
			adjustToRigt();
		}
	}

	@Override
	public void setText(String text) {
		if (doubleTextUsed) {
			this.remove(upperLabel);
			this.remove(bottomLabel);
			this.setLayout(new FlowLayout());
			this.revalidate();
			this.repaint();
			doubleTextUsed = false;
		}
		super.setText(text);
	}

	@Override
	public Dimension getMaximumSize() {
		Dimension superDim = super.getMaximumSize();
		if (doubleTextUsed) {
			Dimension upperDim = upperLabel.getMaximumSize();
			Dimension bottomDim = bottomLabel.getMaximumSize();
			int properWidth = 40;
			if (upperDim.width - bottomDim.width >= 0) {
				properWidth += upperDim.width;
			} else {
				properWidth += bottomDim.width;
			}
			return new Dimension(properWidth, upperDim.height + bottomDim.height + 10);
		} else {
			return superDim;
		}
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension superDim = super.getPreferredSize();
		if (doubleTextUsed) {
			Dimension upperDim = upperLabel.getPreferredSize();
			Dimension bottomDim = bottomLabel.getPreferredSize();
			int properWidth = 40;
			if (upperDim.width - bottomDim.width >= 0) {
				properWidth += upperDim.width;
			} else {
				properWidth += bottomDim.width;
			}
			return new Dimension(properWidth, upperDim.height + bottomDim.height + 10);
		} else {
			return superDim;
		}
	}

	//code economy methods
	private void prepareLabels(String s1, String s2) {
		this.setLayout(new BorderLayout());
		upperLabel = new JLabel(s1, SwingConstants.CENTER);
		bottomLabel = new JLabel(s2, SwingConstants.CENTER);
		this.add(upperLabel, BorderLayout.NORTH);
		this.add(bottomLabel, BorderLayout.SOUTH);
	}

	private void adjustToLeft() {
		upperLabel.setHorizontalAlignment(SwingConstants.LEFT);
		bottomLabel.setHorizontalAlignment(SwingConstants.LEFT);
	}

	private void adjustToCenter() {
		upperLabel.setHorizontalAlignment(SwingConstants.CENTER);
		bottomLabel.setHorizontalAlignment(SwingConstants.CENTER);
	}

	private void adjustToRigt() {
		upperLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		bottomLabel.setHorizontalAlignment(SwingConstants.RIGHT);
	}
}

/*private void checkSizes() {
		Dimension upperDim = upperLabel.getMaximumSize();
		Dimension bottomDim = bottomLabel.getMaximumSize();
		int properWidth = 40;
		if (upperDim.width - bottomDim.width >= 0) {
			properWidth += upperDim.width;
		} else {
			properWidth += bottomDim.width;
		}
		String name1 = upperLabel.getText();
		String name2 = bottomLabel.getText();
		System.out.println("MaxSize of the label *" + name1 + "* is: " + upperDim.toString());
		System.out.println("MaxSize of the label *" + name2 + "* is: " + bottomDim.toString());
		System.out.println("MaxWidth of the button*" + name1 + name2 + "* is: " + properWidth);
	}*/