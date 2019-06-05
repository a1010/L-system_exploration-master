package lsystem;

import serachItem.Cell;
import serachItem.SlidePuzzleNode;

public abstract class Lsystem {

	public abstract void apply(Cell node);
		
	public abstract boolean checkFinish(Cell node);
}
