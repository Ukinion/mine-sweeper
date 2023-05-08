package game.logic;

import java.util.HashMap;

public class PlayerAction
{
    public enum ActionType
    {
        CLICK, FLAG, EXIT_TO_MENU, SAVE_SCORE, REMOVE_SCORE, SERIALIZE_SCORE, INVALID
    }

    private HashMap<String, Object> _actionParameters;
    private ActionType _actionType;

    public PlayerAction()
    {
        _actionParameters = new HashMap<>();
        _actionType = ActionType.INVALID;
    }

    public Object getActionParameters(String action)
    {
        return _actionParameters.get(action);
    }

    public void defineAction(String action, Object actionParameter, ActionType actionType)
    {
        _actionParameters.put(action, actionParameter);
        _actionType = actionType;
    }

    public boolean isInvalidAction(ActionType actionType)
    {
        return actionType == ActionType.INVALID;
    }

    public ActionType getActionType()
    {
        return _actionType;
    }


}
