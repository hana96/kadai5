import java.util.*;
import java.io.*;

public class Salesman{
    //まだ最短経路をファイルに書き込むことはできていません。
    static final int UNKNOWN = -1;
    static final double INFINITE = Double.MAX_VALUE;

    static double[] x;//距離のx座標
    static double[] y;//距離のy座標
    static Vector<Node> nodes;//ノード
    static double[][] memo;//途中経過
    static int start;//スタート地点
    static int[] road;//通った道
    static int r = 1;
 
    static int N;//都市の個数

    public static void main(String[] args) {
	Salesman sales = new Salesman();
	Scanner sc = new Scanner(System.in);
	System.out.println("都市の個数");
	N = sc.nextInt();
	System.out.println("データが入っているファイル名");
	String file = sc.next();
	System.out.println("道のりを書き込むファイル名");
	String ofile = sc.next();
	fileRead(file);
	sales.input(sc);
	double ans = sales.solve();
	System.out.printf("%.2f",(ans == INFINITE) ? -1:ans);//答えの距離を出力
	System.out.println();
	/*
	if(ans!=-1) fileWrite(ofile);*/
    }

    //ファイルの読み込み
    public static void fileRead(String ifile){
	try{
	    File inputfile = new File(ifile);
	    BufferedReader br=new BufferedReader(new FileReader(inputfile));
	    String str=br.readLine();
	    str=br.readLine();
	    x = new double[N];
	    y = new double[N];
	    for(int i = 0;str!=null;i++){
		String[] div=str.split(",");//コンマで区切る
		x[i] = Double.parseDouble(div[0]);  //タブの前
		y[i] = Double.parseDouble(div[1]);  //タブの後
		str=br.readLine();
	    }
	    br.close();
	}catch(FileNotFoundException e){
	    System.out.println("ファイルが見つかりません");
	    System.exit(0);
	}catch(IOException e){
	    System.out.println("IOException");
	    System.exit(0);
	}
    }
    
    //ファイルの書き込み
    public static void fileWrite(String file){
	try{
	    File outputfile = new File(file);
	    FileWriter filewriter = new FileWriter(outputfile);
	    BufferedWriter bw = new BufferedWriter(filewriter);
	    bw.write("index");
	    bw.newLine();
	    for(int i = 0;i < road.length;i++){
		bw.write(""+road[i]);
		bw.newLine();
	    }
	    bw.close();
	}catch(FileNotFoundException e){
	    System.out.println("ファイルが見つかりません");
	    System.exit(0);
	}catch(IOException e){
	    System.out.println("IOException");
	    System.exit(0);
	}
    }	    


    //ノードやエッジの初期化
    void input(Scanner sc) {
	start = 0;
	road = new int[N];
	nodes = new Vector<Node>();
	
	for(int i = 0;i < N;i++){
	    Node node = new Node(i);
	    nodes.add(node);
	}
	for(int i = 0;i < N;i++){
	    for(int j = 0;j < N;j++){
		Node f = nodes.get(i);
		Node t = nodes.get(j);
		double c = distance(x[i],x[j],y[i],y[j]);
		f.edges.add(new Edge(f,t,c));
	    }
	}
    }

    //set bitを返す
    int bs2int(BitSet bs){
	int x1 = 0;
	for(int i = 0,f=1;i < nodes.size();i++,f<<=1)
	    if(bs.get(i)) x1 += f;
	return x1;
    }


    //startからlastまでの最短距離を求める
    public double solve(BitSet bs,int last){
	int idx = bs2int(bs);
	if(memo[idx][last] != UNKNOWN) return memo[idx][last];
	bs.clear(last);
	Node xl = nodes.get(last);
	double ans = INFINITE;
       
	for(int i = bs.nextSetBit(0);i >=0;i=bs.nextSetBit(i+1)){
	    if(bs2int(bs)>=2&&i==start) continue;
	    Node yl = nodes.get(i);
	    Edge e = yl.getEdge(xl);
	    if(e == null) continue;
	    double tmp = solve(bs,i);
	    if(tmp == INFINITE) continue;
	    tmp += e.cost;
	    if(tmp < ans) {
		ans = tmp;
	    }
	}
	bs.set(last);
	memo[idx][last] = ans;
	return ans;
    }
    
    //最短距離を求める
    public double solve(){
	road[0] = start;
	memo = new double[(int)Math.pow(2,nodes.size())][nodes.size()];
	for(int i = 0;i < memo.length;i++)
	    for(int j = 0;j < memo[0].length;j++)
		memo[i][j] = UNKNOWN;
	BitSet bs = new BitSet(nodes.size());
	for(int i = 0;i<nodes.size();i++){
	    bs.clear();
	    bs.set(i);
	    memo[bs2int(bs)][start] = (i==start)?0:INFINITE;
	}
	bs.set(0,nodes.size());
	Node xn = nodes.get(start);
	double ans = INFINITE;
	for(int i = 0;i < nodes.size();i++){
	    if(i == start) continue;
	    Node yn = nodes.get(i);
	    Edge e = yn.getEdge(xn);
	    if(e == null) continue;
	    double tmp = solve(bs,i);
	    if(tmp == INFINITE) continue;
	    tmp += e.cost;
	    if(tmp < ans) {
		ans = tmp;
	    }
	}
	return ans;
    }
    

    //距離を求める
    public static double distance(double x1,double x2,double y1,double y2){
	return Math.sqrt(Math.pow((x1-x2),2)+Math.pow((y1-y2),2));
    }

    //ノードのクラス
    public class Node {
	int id;
	Vector<Edge> edges;
	public Node(int id){
	    this.id = id;
	    edges = new Vector<Edge>();
	}
	public Edge getEdge(Node n){
	    for(Edge e:edges)
		if(e.to == n) return e;
	    return null;
	}
    }

    //エッジのクラス
    public class Edge {
	Node from,to;
	double cost;
	public Edge(Node from,Node to,double cost){
	    this.from = from;
	    this.to = to;
	    this.cost = cost;
	}
    }
}
    
