package lsystem;

import serachItem.Cell;

public abstract class Lsystem {

	public abstract void apply(Cell node);

	public abstract boolean checkFinish(Cell node);
}
