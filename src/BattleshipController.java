package src;
class BattleshipController {
    private BattleshipModel model;

    public BattleshipController(BattleshipModel model) {
        this.model = model;
    }

    public void handleClick(int x, int y) {
        model.attack(x, y);
    }
}