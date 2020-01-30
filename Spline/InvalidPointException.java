public class InvalidPointException extends RuntimeException {
  public InvalidPointException(int i, int m, int n) {
    super("Недопустимый индекс узла: " + i +
          ". Допустимы индексы от " + m + " до " + n);
  }
}
