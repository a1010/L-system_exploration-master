package lsystem;

import serachItem.Cell;
import serachItem.SlidePuzzleNode;
import slidepuzzle_with_Lsystem.SlidePuzzle;

public class SlidePuzzleLsystem extends Lsystem {
	private int max_node_count;

	public SlidePuzzleLsystem(int max_node_count) {
		this.max_node_count = max_node_count;
	}

	@Override
	public void apply(Cell node) {
		SlidePuzzleNode sNode = (SlidePuzzleNode) node;
		if (checkFinish(sNode)) {
			finish(sNode);
			return;
		}
		String state = sNode.getState();
		switch (state) {
		case "0":
			rule_0(sNode);
			break;
		case "2":
			rule_2(sNode);
			break;
		case "E":
			rule_E(sNode);
			break;
		case "D":
			rule_D(sNode);
		case "d":
			rule_d(sNode);
			break;
		default:
			break;
		}
	}

	public void finish(SlidePuzzleNode node) {
		node.setState("F");
	}

	@Override
	public boolean checkFinish(Cell node) {
		SlidePuzzleNode sNode = (SlidePuzzleNode) node;
		if (sNode.getBoardState().equals(SlidePuzzle.getGoalBoardState()))
			return true;
		else
			return false;
	}

	private void rule_0(SlidePuzzleNode node) {
		node.setState("2");
		double random = Math.random();
		if (random < 0.0) {
			String next_state = SlidePuzzle.change(node, "TOP");
			if (next_state != null) {
				String direction = changeDirection(node, "TOP");
				SlidePuzzleNode node2 = new SlidePuzzleNode("0", next_state, direction, node);
				node.addChild(node2);
			}
		} else if (random >= 0.0 && random < 0.0001) {
			String next_state2 = SlidePuzzle.change(node, "LEFT");
			if (next_state2 != null) {
				String direction = changeDirection(node, "LEFT");
				SlidePuzzleNode node3 = new SlidePuzzleNode("0", next_state2, direction, node);
				node.addChild(node3);
			}
			String next_state3 = SlidePuzzle.change(node, "RIGHT");
			if (next_state3 != null) {
				String direction = changeDirection(node, "RIGHT");
				SlidePuzzleNode node4 = new SlidePuzzleNode("0", next_state3, direction, node);
				node.addChild(node4);
			}
		} else {
			String next_state = SlidePuzzle.change(node, "TOP");
			if (next_state != null) {
				String direction = changeDirection(node, "TOP");
				SlidePuzzleNode node2 = new SlidePuzzleNode("0", next_state, direction, node);
				node.addChild(node2);
			}
			String next_state2 = SlidePuzzle.change(node, "LEFT");
			if (next_state2 != null) {
				String direction = changeDirection(node, "LEFT");
				SlidePuzzleNode node3 = new SlidePuzzleNode("0", next_state2, direction, node);
				node.addChild(node3);
			}
			String next_state3 = SlidePuzzle.change(node, "RIGHT");
			if (next_state3 != null) {
				String direction = changeDirection(node, "RIGHT");
				SlidePuzzleNode node4 = new SlidePuzzleNode("0", next_state3, direction, node);
				node.addChild(node4);
			}
		}

	}

	private void rule_2(SlidePuzzleNode node) {
		if (node.getChildrenByArrayList().size() == 0)
			node.setState("d");
	}

	private void rule_E(SlidePuzzleNode node) {
		if (node.getChildrenByArrayList().size() == 0)
			node.setState("d");
	}

	private void rule_D(SlidePuzzleNode node) {
		SlidePuzzleNode parent = node.getParent();
		if (parent != null) {
			if (node.getChildrenByArrayList().size() == 0) {
				parent.setState("d");
				node.dead();
			}
		}
	}

	private void rule_d(SlidePuzzleNode node) {
		if (node.getChildrenByArrayList().size() == 0)
			node.setState("D");
	}

	public String changeDirection(SlidePuzzleNode node, String change_type) {
		String direction = "";
		switch (node.getDirection()) {
		case "8":
			if (change_type.equals("TOP"))
				direction = "8";
			else if (change_type.equals("RIGHT"))
				direction = "6";
			else if (change_type.equals("LEFT"))
				direction = "4";
			else if (change_type.equals("BOTOM"))
				direction = "2";
			break;
		case "6":
			if (change_type.equals("TOP"))
				direction = "6";
			else if (change_type.equals("RIGHT"))
				direction = "2";
			else if (change_type.equals("LEFT"))
				direction = "8";
			else if (change_type.equals("BOTOM"))
				direction = "4";
			break;
		case "4":
			if (change_type.equals("TOP"))
				direction = "4";
			else if (change_type.equals("RIGHT"))
				direction = "8";
			else if (change_type.equals("LEFT"))
				direction = "2";
			else if (change_type.equals("BOTOM"))
				direction = "6";
			break;
		case "2":
			if (change_type.equals("TOP"))
				direction = "2";
			else if (change_type.equals("RIGHT"))
				direction = "4";
			else if (change_type.equals("LEFT"))
				direction = "6";
			else if (change_type.equals("BOTOM"))
				direction = "8";
			break;
		default:
			System.out.println("direction error");
			break;
		}
		return direction;
	}

}
