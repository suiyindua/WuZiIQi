package wuzi2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.transform.Source;

public class Text extends JFrame implements MouseListener,Runnable {
	int width = Toolkit.getDefaultToolkit().getScreenSize().width;
	int height = Toolkit.getDefaultToolkit().getScreenSize().height;
	BufferedImage bg =null;
	//保存棋子坐标
	int x = 0;
	int y = 0;
	//保存之前下过的全部棋子的坐标 0:没棋子 1：黑子 2：白子
	int[][] allchess=new int[18][18];
	//标识当前是黑还是白棋
	boolean isblack = true;
	//判断游戏结束
	boolean canplay = true;
	//保存提示
	String message = "黑方先行";
	//时间
	int time = 0;
	//线程,时间控制
	Thread t = new Thread(this);
	//黑白方的剩余时间
	int blacktime = 0;
	int whitetime = 0;
	//时间信息
	String blackmessage = "无限制";
	String whitemessage = "无限制";
	public Text(){
		this.addMouseListener(this);//为窗体加入监听
		this.setTitle("五子棋");//设置标题
		this.setSize(800, 800);//设置窗体
		
		this.setLocation((width - 1000)/2, (height - 1000)/2);//居中处理
		this.setVisible(true);//显示窗体
		this.setResizable(false);
		t.start();//线程启动
		t.suspend();//线程挂起
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			bg = ImageIO.read(new File("e:/back/background3.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void paint(Graphics g){
		//双缓冲技术防止屏幕闪烁
		BufferedImage b1 =new BufferedImage(1000,1000,BufferedImage.TYPE_INT_ARGB);
		Graphics g2 =b1.createGraphics();
		g.clearRect(0, 0, width, height);
		g.drawImage(bg, 0, 100, this);
		//提示信息
		g.setFont(new Font("黑体", Font.BOLD, 20));
		g.drawString("游戏信息: "+ message, 60, 60);
		//时间信息
		g.setFont(new Font("宋体", 0, 20));
		g.drawString("黑方时间:"+blackmessage, 60, 650);
		g.drawString("白方时间:"+whitemessage, 300, 650);
		//按钮信息
		g.setFont(new Font("华文琥珀", 0, 20));
		g.drawRect(560,170, 100, 50);
		g.drawString("开始游戏", 570, 200);
		g.drawRect(560,320, 100, 50);
		g.drawString("游戏说明", 570, 350);
		g.drawRect(35, 620, 195, 50);
		g.drawRect(285, 620, 195, 50);
		g.drawRect(560,470, 100, 50);
		g.drawString("认输", 585, 500);
		g.drawRect(560, 620, 100, 50);
		g.drawString("游戏设置", 570, 650);
		//绘制棋盘
		g.drawLine(3, 101, 499, 101);
		for(int i=0;i<16;i++)
		{
			g.drawLine(34, 134+(i*31), 499, 134+(i*31));
			g.drawLine(34+(i*31), 134, 34+(i*31), 595);
		}
		//绘制全部棋子
		for(int i =0;i<17;i++){
		
			for(int j =0;j<17;j++){
				if(allchess[i][j]==1){
					int tx = i*31+3;
					int ty = j*31+101;
					g.fillOval(tx-8, ty-8, 16, 16);
				}
				if(allchess[i][j]==2){
					int tx = i*31+3;
					int ty = j*31+101;
					g.setColor(Color.white);
					g.fillOval(tx-8, ty-8, 16, 16);
					g.setColor(Color.BLACK);
					g.drawOval(tx-8, ty-8, 16, 16);
				}
			}
		}
	}
	//棋子数量判断

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param e
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		//System.out.println(e.getX());
		//System.out.println(e.getY());
		if(canplay == true){
		x =e.getX();
		y =e.getY();
		if(x>=34&&x<=499 &&y>=132&&y<=595){
			x = (x-3)/31;
			y = (y-101)/31;
			if(allchess[x][y]==0){
			if(isblack==true){
				allchess[x][y] = 1;
				isblack = false;
				message = "轮到白方";
			}
			else{
				allchess[x][y] = 2;
				isblack = true;
				message = "轮到黑方";
			}	
			//信息交替功能
			boolean winr = this.cwin();//判断胜负
			if(winr == true)
			{
				
				JOptionPane.showMessageDialog(this, "游戏结束,"+
				(allchess[x][y] == 1 ? "黑方":"白方") + "获胜!");
				canplay = false;
				
			}
			}
			else{
				JOptionPane.showMessageDialog(this, "当前位置有棋子，请重新落子");
			}
			
			this.repaint();
			}

		}
		//开始游戏按钮功能
	if(e.getX()>=560&&e.getX()<=660&&e.getY()>=170&&e.getY()<=220){
	int result4 = JOptionPane.showConfirmDialog(this, "是否重新开始游戏");
	if(result4==0){
		//数组归零
		for(int i=0;i<17;i++)
		{
			for(int j=0;j<17;j++)
			{
				allchess[i][j] = 0;
			}
		}
		//游戏信息
		message = "黑方先行";
		isblack = true;
		canplay = true;
		blacktime = time;
		whitetime = time;
		if(time>0){
			blackmessage = (time/3600)+":"+(time/60-time/3600*60)+":"+(time-time/60*60);
			whitemessage = (time/3600)+":"+(time/60-time/3600*60)+":"+(time-time/60*60);	
			t.resume();//启动线程
		}else{
			blackmessage = "无限制";
			whitemessage = "无限制";
		}
		this.repaint();
	}
	}
	//游戏说明功能
	if(e.getX()>=560&&e.getX()<=660&&e.getY()>=320&&e.getY()<=370){
		JOptionPane.showMessageDialog(this, "点击两线交叉点的右下角空白区域即能在两线交叉处下棋，当任意五个相同棋子连接，则胜利");
		}
	//认输按钮功能
	if(e.getX()>=560&&e.getX()<=660&&e.getY()>=470&&e.getY()<=520){
		int result2 = JOptionPane.showConfirmDialog(this, "是否确认认输?");
		if(result2 == 0){
			if(isblack){
				JOptionPane.showMessageDialog(this, "黑方已经认输，游戏结束");
				canplay = false;
			}
			else{
				JOptionPane.showMessageDialog(this, "白方已经认输，游戏结束");
				canplay = false;
			}
		}
		}
	//游戏设置功能
	if(e.getX()>=560&&e.getX()<=660&&e.getY()>=620&&e.getY()<=670){
		String put =JOptionPane.showInputDialog("输入游戏的最大时间(分),如果输入0，表示无时间限制");
		try {
			time = Integer.parseInt(put)*60;//强制类型转换
			if(time<0){
				JOptionPane.showMessageDialog(this, "请输入正确时间信息，不能输负数");
			}
			if(time >= 0){
				int result5 = JOptionPane.showConfirmDialog(this, "设置完成,是否重新开始游戏");
				if (result5 == 0){
					for(int i=0;i<17;i++)
					{
						for(int j=0;j<17;j++)
						{
							allchess[i][j] = 0;
						}
					}
				}
				message = "黑方先行";
				isblack = true;
				canplay = true;
				blacktime = time;
				whitetime = time;				
				blackmessage = (time/3600)+
						":"+(time/60-time/3600*60)+":"+
						(time-time/60*60);
				whitemessage = (time/3600)+
						":"+(time/60-time/3600*60)+":"+
						(time-time/60*60);	
					t.resume();//启动线程
				this.repaint();//游戏复原功能
			}
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(this, "请输入时间信息");
	}
}
	}
	//private void (String put) {
		//time = Integer.parseInt(put)*60;
	//}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	private boolean cwin(){
		boolean flag = false;
		//保存棋子相连数量
		int count = 1;
		//横向判断
		int color = allchess[x][y];
		int i=1;
		while(color == allchess[x+i][y]){
			count++;
			i++;
			
		}
		i=1;
		while(color == allchess[x-i][y]){
			count++;
			i++;
		}
		if(count >=5){
			flag = true;
			canplay =false;
		}
		//纵向
		int i2 =1;
		int count2 = 1;
		while(color == allchess[x][y+i2]){
			count2++;
			i2++;
			
		}
		i2=1;
		while(color == allchess[x][y-i2]){
			count2++;
			i2++;
		}
		if(count2>=5){
			flag = true;
			canplay =false;
		}
		//正斜向判断
		int i3 =1;
		int count3 =1;
		while(color == allchess[x+i3][y-i3]){
			count3++;
			i3++;
			
		}
		i3=1;
		while(color == allchess[x-i3][y+i3]){
			count3++;
			i3++;
		}
		if(count3>=5){
			flag = true;
			canplay =false;
		}
		//反斜向判断
		int i4=1;
		int count4=1;
		while(color == allchess[x-i4][y-i4]){
			count4++;
			i4++;
			
		}
		i4=1;
		while(color == allchess[x+i4][y+i4]){
			count4++;
			i4++;
		}
		if(count4>=5){
			flag = true;
			canplay =false;
		}
		return flag;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(time > 0){
			while(true){
				if(isblack){
					blacktime--;
					if(blacktime == 0){
						JOptionPane.showMessageDialog(this, "黑方超时，游戏结束!");

						canplay = false;
						
					}
					
				}
				else{
					whitetime--;
					if(whitetime == 0){
						JOptionPane.showMessageDialog(this, "白方超时，游戏结束!");

						canplay = false;
						
				}
				}
				try {
					Thread.sleep(1000);//1000ms休眠
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				blackmessage = (blacktime/3600)+
						":"+(blacktime/60-blacktime/3600*60)+":"+
						(blacktime-blacktime/60*60);
				whitemessage = (whitetime/3600)+
						":"+(whitetime/60-whitetime/3600*60)+":"+
						(whitetime-whitetime/60*60);
				this.repaint();
				
			}
		}
	}//倒计时功能
}
