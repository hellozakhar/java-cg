public class InvalidSegmentException extends RuntimeException {
  public InvalidSegmentException(int i, int n) {
    super("Недопустимый индекс сегмента: " + i + ". Допустимы индексы от 0 до " + n);
  }
}
