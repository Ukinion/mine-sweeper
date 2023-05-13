package game.model;

import game.exception.GameException;
import game.objects.field.MineField;
import game.objects.unit.Cell;
import game.objects.unit.ScoreTable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Timer;
import java.util.TimerTask;

public class MineSweeper
{
    public static final String IGNORE_EVENT = "GameTimeOrCellChange";
    public static final String CHANGE_GAME_STAGE_EVENT = "StageChange";
    public static final String REQUEST_ACTION_EVENT = "NothingChange";
    public static final String FIELD_CHANGE_EVENT = "FieldChangeOrFlagSet";

    private static final int BASE_SCORE = 1;
    private static final int DELAY = 1000;
    private static final int INTERVAL = 1000;
    private static final Object IGNORE = null;
    private static final int START_NEIGHBOUR = -1;
    private static final int END_NEIGHBOUR = 2;

    public String getAboutInfo()
    {
        return "\t\tWelcome to Minesweeper!\n\t" +
                "1) Click on titles to open them.\n\t" +
                "2) Numbers show count of bombs around (3x3 area).\n\t" +
                "3) Try not to blow up!";
    }

    public enum GameStage
    {
        LAUNCHED, ACTION, VICTORY, DEFEAT, CLOSED
    }

    private GameStage _gameStage;
    private MineField _gameField;
    private ScoreTable _scoreTable;

    private Timer _gameTimer;
    private PropertyChangeListener _gameListener;
    private PropertyChangeSupport _gameEventSupport;

    private int _curScore;
    private int _curGameTime;
    private int _cellToOpen;
    private boolean _isFirstClick = true;


    public MineSweeper()
    {
        _gameEventSupport = new PropertyChangeSupport(this);
        _scoreTable = new ScoreTable();
    }

    public void startGame(int gameFieldRow, int gameFieldCol, int numMinesOnField) throws GameException
    {
        _gameField = new MineField(gameFieldRow, gameFieldCol, numMinesOnField);
        _cellToOpen = _gameField.getNumNotMinedCell();
        _curScore = BASE_SCORE;
        _gameStage = GameStage.LAUNCHED;

        _gameTimer = new Timer();
        _curGameTime = 0;

        changeStageAndNotify(GameStage.ACTION);
    }

    public void addListener(PropertyChangeListener newListener)
    {
        _gameEventSupport.addPropertyChangeListener(newListener);
    }

    private void changeStageAndNotify(GameStage newStage)
    {
        _gameEventSupport.firePropertyChange(CHANGE_GAME_STAGE_EVENT, _gameStage, newStage);
        _gameStage = newStage;
    }

    public void clickOnCell(int x, int y)
    {
        if (isIgnoreClick(x,y))
        {
            _gameEventSupport.firePropertyChange(REQUEST_ACTION_EVENT, IGNORE, IGNORE);
        }
        else processClick(_gameField.locateCell(x,y));
    }

    private boolean isIgnoreClick(int x, int y)
    {
        return isClickOutOfField(x,y) || isClickOnFlagOrOpenCell(_gameField.locateCell(x,y));
    }

    private boolean isClickOutOfField(int x, int y)
    {
        return x >= _gameField.getFieldCol() || y >= _gameField.getFieldRow();
    }

    private boolean isClickOnFlagOrOpenCell(Cell cell)
    {
        return cell.isOpened() || cell.isFlag();
    }

    private void processClick(Cell cell)
    {
        if (cell.isMine())
        {
            if (_isFirstClick)
            {
                startGameTimer();
                _gameField.moveMine(cell);
            }
            else
            {
                Defeat();
                return;
            }
        }
        openCellLocality(cell.getCoordinateX(), cell.getCoordinateY());
        if (!isVictory())
        {
            _gameEventSupport.firePropertyChange(FIELD_CHANGE_EVENT, IGNORE, cell);
        }
    }

    public void startGameTimer()
    {
        TimerTask gameTimerTask = new TimerTask() {
            @Override
            public void run() {
                _gameEventSupport.firePropertyChange(IGNORE_EVENT,
                        IGNORE, ++_curGameTime);
                updateCurGameScore();
            }
        };
        _gameTimer.schedule(gameTimerTask, DELAY, INTERVAL);
    }

    private void updateCurGameScore()
    {
        _curScore += _curScore / _curGameTime;
    }

    private void Defeat()
    {
        _gameField.openField();
        changeStageAndNotify(GameStage.DEFEAT);
        resetGameTimer();
    }

    public void resetGameTimer()
    {
        _gameTimer.cancel();
        _gameTimer.purge();
    }

    private void openCellLocality(int x, int y)
    {
        if (_gameField.isOutOfField(x, y)) return;

        Cell cell = _gameField.locateCell(x, y);
        if (cell.isGetRound()) return;

        cell.getRound();
        if (cell.isMine() || cell.isOpened()) return;

        cell.openCell();
        _gameEventSupport.firePropertyChange(IGNORE_EVENT, IGNORE, cell);
        if (cell.getMinesAround() > 0 || _cellToOpen-- == 0) return;

        for (var i = START_NEIGHBOUR; i < END_NEIGHBOUR; ++i)
        {
            for (var j = START_NEIGHBOUR; j < END_NEIGHBOUR; ++j)
            {
                openCellLocality(x-j, y-i);
            }
        }
    }

    private boolean isVictory()
    {
        if (_cellToOpen == 0)
        {
            _gameField.openField();
            changeStageAndNotify(GameStage.VICTORY);
            return true;
        }
        return false;
    }

    public void closeGame()
    {
        changeStageAndNotify(GameStage.CLOSED);
    }

    public ScoreTable getScoreTable()
    {
        return _scoreTable;
    }

    public MineField getGameField()
    {
        return _gameField;
    }


    public void setFlag(int x, int y)
    {
        _gameField.locateCell(x,y).setFlag();
        _gameEventSupport.firePropertyChange(FIELD_CHANGE_EVENT, IGNORE, _gameField.locateCell(x,y));
    }

    public int getCurGameTime()
    {
        return _curGameTime;
    }

    public int getCurScore()
    {
        return _curScore;
    }


}
