package serachItem;

public abstract class Cell {
	// 状態保持
	protected String state;
	// 次状態保持
	protected String next_state;
	// 死滅フラグ
	protected boolean deadflag = false;

	public Cell() {
	}

	public String getState() {
		return state;
	}

	public abstract void update();

	public abstract void setState(String state);

}
