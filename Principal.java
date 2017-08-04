import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;
import javax.imageio.*;

class Principal extends JFrame {
  int classe; //1 - Cacador; 2 - Presa
  int passo;
  int tparado;
  int projetil;
  int direcao;
  int pontos;
  boolean mostraPasso;
  boolean jogoAtivo;
  boolean comecar;
  boolean finalizando;
  Double b, m, dist;
  final int wwidth = 1200;
  final int wheight = 900;
  final int tamanho = 30;
  final int esq = 1;
  final int dir = 2;
  final int cim = 3;
  final int bai = 4;
  volatile boolean serverStart, clientStart;
  Image imgPasso1, imgPasso2, imgGuizos1;
  Desenho d;
  Posicao jogador, inimigo, tiro;
  Guizos gu;
  Color bg, ch;
  Random r;
  Queue<Passos> passoIn = new ConcurrentLinkedQueue<Passos>();
  Queue<Sons> somIn = new ConcurrentLinkedQueue<Sons>();

  ObjectOutputStream os;
  ObjectInputStream is;
  Passos passoOut;
  Sons somOut;

  Principal() {
    super("Paranoia");
    serverStart = false;
    clientStart = false;
    comecar = false;
    new ThreadServer().start();
    while(!serverStart);
    bg = new Color(0, 0, 0);
    ch = new Color(255, 255, 255);
    passo = 0;
    mostraPasso = false;
    jogoAtivo = true;
    finalizando = true;
    tparado = 180;
    projetil = 0;
    pontos = 0;
    try {
      imgPasso1 = ImageIO.read(new File("images/passo1.png"));
      imgPasso2 = ImageIO.read(new File("images/passo2.png"));
      imgGuizos1 = ImageIO.read(new File("images/guizos1.png"));
    } catch(Exception e) {
      e.printStackTrace();
    }
    r = new Random();
    d = new Desenho();
    tiro = new Posicao(0, 0);
    inimigo = new Posicao(0, 0);
    somOut = new Sons(0, 0, 255, 255, 255);
    try {
      classe = is.readInt();
    } catch(Exception e) {
      e.printStackTrace();
    }
    clientStart = true;

    while(!comecar) {
      System.out.println("Aguardando o outro jogador...");
      try {
        Thread.sleep(1000);
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
    System.out.println("CLASSE: " + classe);
    if(classe == 1) {
      jogador = new Posicao(0, 0);
    } else {
      jogador = new Posicao(wwidth, wheight);
    }
    if(classe == 1) {
      d.addMouseListener(new ListenerShot());
    } else {
      gu = new Guizos(r.nextInt(wwidth / tamanho) * tamanho, r.nextInt(wheight / tamanho) * tamanho);
    }
    new ThreadDraw().start();

    addKeyListener(new ListenerMovement());
    addWindowListener(adapter);
    add(d);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setResizable(false);
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }

  WindowAdapter adapter = new WindowAdapter() {
    @Override
    public void windowClosing(WindowEvent e) {
      try {
        jogoAtivo = false;
        os.writeObject(false);
        os.reset();
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    };
  };

  class Desenho extends JPanel {
    Sons s;
    Passos p;
    Desenho() {
      setPreferredSize(new Dimension(wwidth, wheight));
      setSize(new Dimension(wwidth, wheight));
      setMaximumSize(new Dimension(wwidth, wheight));
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.setColor(bg);
      g.setFont(new Font("Verdana", Font.BOLD, 20));
      g.fillRect(0, 0, wwidth, wheight);

      Iterator<Sons> its = somIn.iterator();
      while(its.hasNext()) {
        s = its.next();
        s.setRaio(s.getRaio() + 3);
        g.setColor(s.getColor());
        g.drawOval(s.getX() - s.getRaio() / 2, s.getY() - s.getRaio() / 2, s.getRaio(), s.getRaio());
        if(s.getRaio() >= 150) its.remove();
      }

      if(classe == 2) {
        g.drawImage(imgGuizos1, gu.getX() - tamanho / 2, gu.getY() - tamanho / 2, tamanho, tamanho, null);
      }

      if(projetil > 0) {
        projetil--;
        if (classe == 1) {
          if(projetil > 15) {
            g.setColor(new Color((projetil - 15) * 255 / 30, 0, 0));
            g.drawLine(jogador.getX(), jogador.getY(), tiro.getX(), tiro.getY());
          }
          if(projetil > 15) {
            g.setColor(new Color(255, 255, 255));
          } else {
            g.setColor(new Color(projetil * 255 / 15, projetil * 255 / 15, projetil * 255 / 15));
          }
          if(jogador.getX() > wwidth / 2) {
            g.drawRect(jogador.getX() - 2 * tamanho, (int) (jogador.getY() - 0.5 * tamanho), tamanho, tamanho);
            g.fillRect(jogador.getX() - 2 * tamanho, (int) (jogador.getY() - 0.5 * tamanho), tamanho - projetil * tamanho / 45, tamanho);
          } else {
            g.drawRect(jogador.getX() + tamanho, (int) (jogador.getY() - 0.5 * tamanho), tamanho, tamanho);
            g.fillRect(jogador.getX() + tamanho, (int) (jogador.getY() - 0.5 * tamanho), tamanho - projetil * tamanho / 45, tamanho);
          }
        } else if(projetil > 15) {
          g.setColor(new Color((projetil - 15) * 255 / 30, 0, 0));
          g.drawLine(inimigo.getX(), inimigo.getY(), tiro.getX(), tiro.getY());
        }
      }

      g.setColor(ch);
      g.fillOval(jogador.getX() - tamanho / 2, jogador.getY() - tamanho / 2, tamanho, tamanho);
      if(classe == 2) {
        g.drawString("Guizos coletados: " + pontos, 0, 20);
      }
      Iterator<Passos> itp = passoIn.iterator();
      while(itp.hasNext()) {
        p = itp.next();
        p.setOpacidade(p.getOpacidade() - 0.03f);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, p.getOpacidade()));
        switch(p.getDirecao()) {
          case esq:
            g2d.drawImage(imgPasso2, p.getX() + tamanho / 2, p.getY() - tamanho / 2, -tamanho, tamanho, null);
            break;
          case dir:
            g2d.drawImage(imgPasso2, p.getX() - tamanho / 2, p.getY() - tamanho / 2, tamanho, tamanho, null);
            break;
          case cim:
            g2d.drawImage(imgPasso1, p.getX() - tamanho / 2, p.getY() - tamanho / 2, tamanho, tamanho, null);
            break;
          case bai:
            g2d.drawImage(imgPasso1, p.getX() - tamanho / 2, p.getY() + tamanho / 2, tamanho, -tamanho, null);
            break;
        }
        if(p.getOpacidade() <= 0.1) itp.remove();
      }
    }
  }

  class ListenerMovement extends KeyAdapter {
    public void keyPressed(KeyEvent e) {
      switch(e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
          direcao = esq;
          break;
        case KeyEvent.VK_RIGHT:
          direcao = dir;
          break;
        case KeyEvent.VK_UP:
          direcao = cim;
          break;
        case KeyEvent.VK_DOWN:
          direcao = bai;
          break;
      }
    }

    public void keyReleased(KeyEvent e) {
      direcao = 0;
    }
  }

  class ListenerShot extends MouseAdapter {
    public void mouseReleased(MouseEvent e) {
      int offset = 0;
      Posicao t = new Posicao(0, 0);
      t.setX((int) e.getPoint().getX());
      t.setY((int) e.getPoint().getY());
      if(t.getX() - jogador.getX() != 0 && projetil == 0) {
        m = (double) (t.getY() - jogador.getY()) / (t.getX() - jogador.getX());
        if(tparado <= 0 && tparado > -100) {
          offset = -r.nextInt() % (-tparado);
        } else if (tparado <= -100) {
          offset = r.nextInt() % 100;
        }
        //y = mx + b; b = y - mx;
        //x = (b - y) / m;
        b = jogador.getY() - m * jogador.getX();
        if(t.getX() < jogador.getX() && b >= 0 && b <= wheight) {
          tiro.setX(0);
          tiro.setY((int) Math.floor(b) + offset);
        } else if (t.getX() > jogador.getX() && m * wwidth + b >= 0 && m * wwidth + b <= wheight) {
          tiro.setX(wwidth);
          tiro.setY((int) Math.floor(m * wwidth + b) + offset);
        } else if (t.getY() < jogador.getY() && (-b) / m >= 0 && (-b) / m <= wwidth) {
          tiro.setX((int)Math.floor((-b) / m) + offset);
          tiro.setY(0);
        } else if (t.getY() > jogador.getY() && (wheight - b) / m >= 0 && (wheight - b) / m <= wwidth) {
          tiro.setX((int)Math.floor((wheight - b) / m) + offset);
          tiro.setY(wheight);
        }
        projetil = 45;

        try {
          os.writeObject(tiro);
          os.reset();
          os.writeObject(jogador);
          os.reset();
        } catch(Exception ex) {
          ex.printStackTrace();
        }

        if(tparado > 0) {
          tparado = 1;
        }
      }
    }
  }


  class ThreadDraw extends Thread {
    int movx, movy;
    public void run() {
      while(jogoAtivo) {
        try {
          Thread.sleep(1000/30);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        movx = 0;
        movy = 0;
        switch(direcao) {
          case esq:
            movx = -tamanho / 2;
            break;
          case dir:
            movx = tamanho / 2;
            break;
          case cim:
            direcao = cim;
            movy = -tamanho / 2;
            break;
          case bai:
            direcao = bai;
            movy = tamanho / 2;
            break;
        }

        if(jogador.getX() + movx <= wwidth && jogador.getX() + movx >= 0
          && jogador.getY() + movy <= wheight && jogador.getY() + movy >= 0
          && (classe == 1 && projetil == 0 || classe == 2)
          && (movx != 0 || movy != 0)) {
            passo++;
            tparado = 180;
            jogador.setX(jogador.getX() + movx);
            jogador.setY(jogador.getY() + movy);
            if(classe == 2
              && Math.abs(jogador.getX() - gu.getX()) <= tamanho / 2
              && Math.abs(jogador.getY() - gu.getY()) <= tamanho / 2) {
              pontos++;
              somOut = new Sons(gu.getX(), gu.getY(), 255, 255, 0);

              try {
                os.writeObject(somOut);
                os.reset();
              } catch (Exception ex) {
                ex.printStackTrace();
              }
              gu = new Guizos(r.nextInt(wwidth / tamanho) * tamanho, r.nextInt(wheight / tamanho) * tamanho);
            }
          }

        if(passo == 15) {
          mostraPasso = true;
          somOut = new Sons(jogador.getX(), jogador.getY(), 255, 255, 255);
          passoOut = new Passos(jogador.getX(), jogador.getY(), direcao);
          try {
            os.writeObject(somOut);
            os.reset();
            os.writeObject(passoOut);
            os.reset();
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          passo = 0;
        }

        tparado--;
        if(tparado <= 0 && tparado * (-1) % 40 == 0) {
          somOut = new Sons(jogador.getX(), jogador.getY(), 255, 0, 0);
          try {
            if (jogoAtivo){
              os.writeObject(somOut);
              os.reset();
            }
          } catch(Exception e) {
            e.printStackTrace();
          }
        }
        repaint();
      }
      if(classe == 1) {
        JOptionPane.showMessageDialog(null, "Fim do jogo! Voce abateu a presa!");
      } else {
        JOptionPane.showMessageDialog(null, "Fim do jogo! Foram coletados " + pontos + " guizos!");
      }
      dispose();
    }
  }

  class ThreadServer extends Thread {
    public void run(){
      Socket socket = null;
      Object obj = null;
      while(socket == null) {
        try {
          socket = new Socket("127.0.0.1", 8080);
          os = new ObjectOutputStream(socket.getOutputStream());
          is = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
          System.out.println("Esperando pelo servidor...");
        }
      }

      serverStart = true;
      System.out.println("Conexao com o servidor estabelecida!");
      while(!clientStart);
      while (jogoAtivo) {
        try {
          obj = is.readObject();
          if(obj instanceof Sons) {
            somIn.add((Sons) obj);
          } else if(obj instanceof Passos) {
            passoIn.add((Passos) obj);
          } else if(obj instanceof Posicao && classe == 2) {
            tiro = (Posicao) obj;
            obj = is.readObject();
            inimigo = (Posicao) obj;
            projetil = 45;
            m = (double)(inimigo.getY() - tiro.getY()) / (inimigo.getX() - tiro.getX());
            b = inimigo.getY() - m * inimigo.getX();
            dist = Math.abs((-m) * jogador.getX() + jogador.getY() - b) / Math.sqrt(m * m + 1);
            if(dist < tamanho / 2 + 5) {
              //jogoAtivo = false;
              os.writeObject(false);
              os.reset();
            }
          } else if(obj instanceof Boolean) {
            if((Boolean) obj) {
              comecar = true;
            } else {
              jogoAtivo = false;
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      try {
        if (classe == 1){
          os.writeObject(false);
          os.reset();
        }
        is.close();
        os.close();
        socket.close();
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    new Principal();
  }
}
