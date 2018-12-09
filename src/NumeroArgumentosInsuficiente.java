public class NumeroArgumentosInsuficiente extends Exception {
    public NumeroArgumentosInsuficiente(String message) {
        super(message);
    }

    public NumeroArgumentosInsuficiente(int nargsNecessario) {
        super(Integer.toString(nargsNecessario));
    }
}
