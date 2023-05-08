package game.objects.field;

import game.objects.unit.Cell;
import game.exception.GameException;
import game.exception.InvalidFieldSetupException;

import java.util.Random;
import java.util.HashMap;

public class MineField
{
    private final static int MIN_FIELD_SIZE = 1;
    private final static int MIN_MINES_ON_FIELD = 1;
    private final static int LINE_ENVIRONMENT = 2;

    private enum PlaceType
    {
       CORNER(3), INNER(8), OUTER(5);
       private final int _ordinal;

       PlaceType(int num)
       {
           _ordinal = num;
       }
    }

    private Cell[] _cells;
    private HashMap<Cell, Cell[]> _neighboursMap;
    private final int _fieldCol;
    private final int _fieldRow;
    private final int _fieldSize;
    private int _numMinesOnField;

    public MineField(int numRow, int numCol, int numMines) throws GameException
    {
        if (numRow < MIN_FIELD_SIZE || numCol < MIN_FIELD_SIZE || numMines < MIN_MINES_ON_FIELD)
        {
            throw new InvalidFieldSetupException();
        }

        _fieldRow = numRow;
        _fieldCol = numCol;
        _fieldSize = numRow* numCol;
        mineBoard(numMines);

        _cells = new Cell[_fieldSize];
        for (var i =0; i < _fieldSize; ++i)
        {
            _cells[i] = new Cell(i % _fieldCol, i / _fieldCol);
        }
        fillOutMap();
    }

    private void fillOutMap()
    {
        int x, y, idx = 0;

        _neighboursMap = new HashMap<>();
        for (var i = 0; i < _fieldSize; ++i)
        {
            x = _cells[i].getCoordinateX();
            y = _cells[i].getCoordinateY();

            Cell[] neighbours = new Cell[getCellNumNeighbours(x, y)];
            for (var j = -1; j < LINE_ENVIRONMENT; ++j)
            {
                for (var k = -1; k < LINE_ENVIRONMENT; ++k)
                {
                    if (isOutOfField(x+k, y+j) || (k == 0  && j == 0)) continue;
                    neighbours[idx++] = _cells[(y+j)*_fieldCol+x+k];
                }
            }
            _neighboursMap.put(_cells[i], neighbours);
            idx = 0;
        }
    }

    private int getCellNumNeighbours(int x, int y)
    {
        if (x == 0 || x == (_fieldCol - 1))
        {
            if (y == 0 || y == (_fieldRow - 1))
            {
                return PlaceType.CORNER._ordinal;
            }
            return PlaceType.OUTER._ordinal;
        }
        return PlaceType.INNER._ordinal;
    }

    public boolean isOutOfField(int x, int y)
    {
        return x < 0 || y < 0 || x >= _fieldCol || y >= _fieldRow;
    }

    private void mineBoard(int numMines)
    {
        if (numMines < _fieldSize)
        {
            for (var i = 0; i < numMines; ++i)
            {
                mineRandomGround();
            }
        }
        else
        {
            _numMinesOnField = _fieldSize;
        }
    }

    private void mineRandomGround()
    {
        Random placeGenerator = new Random();
        Cell cell;

        while (true)
        {
            cell = locateCell(placeGenerator.nextInt(_fieldCol), placeGenerator.nextInt(_fieldRow));
            if (cell.isClear())
            {
                cell.setMine();
                increaseMineAroundNeighbours(cell);
                break;
            }
        }
    }

    public Cell locateCell(int x, int y)
    {
        return _cells[y*_fieldCol+x];
    }

    private void increaseMineAroundNeighbours(Cell cell)
    {
        var neighbours = _neighboursMap.get(cell);
        for (Cell neighbour : neighbours)
        {
            neighbour.detectNewMine();
        }
    }

    public void moveMine(Cell mine)
    {
        mineRandomGround();
        mine.defuseMine(locateNumMineAround(mine));
        decreaseMineAroundNeighbours(mine);
    }

    private int locateNumMineAround(Cell cell)
    {
        int mineAround = 0;

        var neighbours = _neighboursMap.get(cell);
        for (Cell neighbour : neighbours)
        {
            if (neighbour.isMine()) ++mineAround;
        }
        return mineAround;
    }

    private void decreaseMineAroundNeighbours(Cell cell)
    {
        var neighbours = _neighboursMap.get(cell);
        for (Cell neighbour : neighbours)
        {
            neighbour.concealMine();
        }
    }

    public int getNumNotMinedCell()
    {
        return _fieldSize-_numMinesOnField;
    }

    public void openField()
    {
        for (Cell i : _cells)
        {
            i.openCell();
        }
    }

    public int getFieldRow()
    {
        return _fieldRow;
    }

    public int getFieldCol()
    {
        return _fieldCol;
    }
}
