package game.objects.unit;

public class Cell {
    public enum CellType {
        GROUND, MINE
    }

    private CellType _cellType;
    private int _x;
    private int _y;
    private int _numMinesAround;
    private boolean _isOpened;
    private boolean _isFlag;
    private boolean _isGetRound;

    public Cell(int x, int y) {
        _cellType = CellType.GROUND;
        _x = x;
        _y = y;
        _numMinesAround = 0;
        _isOpened = false;
        _isFlag = false;
        _isGetRound = false;
    }

    public boolean isOpened() {
        return _isOpened;
    }

    public boolean isFlag()
    {
        return _isFlag;
    }

    public boolean isGetRound() {
        return _isGetRound;
    }

    public boolean isClear() {
        return _cellType == CellType.GROUND;
    }

    public boolean isMine() {
        return _cellType == CellType.MINE;
    }

    public int getMinesAround() {
        return _numMinesAround;
    }

    public int getCoordinateX() {
        return _x;
    }

    public int getCoordinateY() {
        return _y;
    }

    public void setMine() {
        _cellType = CellType.MINE;
        _numMinesAround = 0;
    }

    public void defuseMine(int numMinesAround) {
        _cellType = CellType.GROUND;
        _numMinesAround = numMinesAround;
    }

    public void detectNewMine() {
        ++_numMinesAround;
    }

    public void openCell() {
        _isOpened = true;
    }

    public void setFlag() {
        _isFlag = true;
    }

    public void removeFlag() {
        _isFlag = false;
    }

    public void getRound() {
        _isGetRound = true;
    }

    public void concealMine() {
        --_numMinesAround;
    }
}
