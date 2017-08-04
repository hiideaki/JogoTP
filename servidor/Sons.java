import java.awt.Color;
import java.io.*;

class Sons extends Posicao implements Serializable {
  private int raio;
  private int r;
  private int g;
  private int b;
  Sons(int x, int y, int r, int g, int b) {
    super(x, y);
    this.raio = 0;
    this.r = r;
    this.g = g;
    this.b = b;
  }

  public void setRaio(int raio) {
    this.raio = raio;
  }

  public int getRaio() {
    return raio;
  }

  public Color getColor() {
    return new Color(r - raio * r / 200, g - raio * g / 200, b - raio * b / 200);
  }
}
