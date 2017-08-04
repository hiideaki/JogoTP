import java.io.*;

class Passos extends Posicao implements Serializable {
  private int direcao;
  private float opacidade;

  Passos(int x, int y, int direcao) {
    super(x, y);
    this.direcao = direcao;
    this.opacidade = 1.0f;
  }

  public void setOpacidade(float opacidade) {
    this.opacidade = opacidade;
  }

  public float getOpacidade() {
    return opacidade;
  }

  public void setDirecao(int direcao) {
    this.direcao = direcao;
  }

  public int getDirecao() {
    return direcao;
  }

}
