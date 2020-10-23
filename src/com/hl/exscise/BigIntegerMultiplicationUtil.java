package com.hl.exscise;

import java.util.Scanner;

public class BigIntegerMultiplicationUtil {

    public static int[] X;	//二进制数组X
    public static int[] Y;	//二进制数组Y
    private static String StringX;//用户输入的X
    private static String StringY;

    private static int MaxLength;//乘积应该开辟的数组长度

    public static void InputInteger(){
        Scanner sc=new Scanner(System.in);
        System.out.println("输入X：");
        StringX = sc.nextLine();
        System.out.println("输入Y：");
        StringY = sc.nextLine();

        X = new int[StringX.length()];
        Y = new int[StringY.length()];
        RawToBinary(StringX, X);
        RawToBinary(StringY, Y);

        MaxLength = X.length * Y.length;
    }


    //将用户输入的字符串，转换为二进制数组binary
    private static void RawToBinary(String raw,int[] binary){
        for(int i = 0; i < raw.length(); i++){
            binary[i] = raw.charAt(i) - '0';
        }
    }



    public static int[] BigIntegerMultiplication(int[] x, int[] y)
    {
        //获取二进制数组的有效位数（比如00001的有效位数为1，其中，默认00000的有效位数为1）
        int xLength = GetEffctiveLength(x);
        int yLength = GetEffctiveLength(y);
        //获取二进制数组有效位数开始的下标，比如00001的Start为4，其中默认00000的Start为0
        int xStart = GetStartWith(x) == -1 ? 0 : GetStartWith(x);
        int yStart = GetStartWith(y) == -1 ? 0 : GetStartWith(y);

        //递归终止条件，即，若某个数组的有效长度为1，就可以计算了
        if (xLength == 1)
        {
            return SimpleMulti(y, x[x.length-1]);
        }
        if (yLength == 1)
        {
            return SimpleMulti(x, y[y.length-1]);
        }

        //**分解过程，n代表数组的中点下标**//
        //假设x为10000，则n1 = 5/2 = 2 ；y为0010，则n2 = 2/2 = 1
        int n1 = xLength / 2;
        int n2 = yLength / 2;

        //获取子数组，还是以x为10000，y为0010例，由于MaxLength = 5 * 4 = 20，所以
        //A = 0000 0000 0000 0000 0010（20位）
        //B = 0000 0000 0000 0000 0000（20位）
        //（这里其实做的很瓜皮，根本没必要这么搞（指把这里面出现的所有数组都统一长度），可是当时想着偷懒，不用算长度了，没想到后面却搞出一堆问题，哎，说明我得层数还不够啊，以后有时间再进行优化，今天懒得搞了，等等搞Xamarin去了）
        int[] A = SubIntArray(x, xStart, xStart + n1 - 1);
        int[] B = SubIntArray(x, xStart + n1, x.length - 1);

        //C = 0000 0000 0000 0000 0001（20位）
        //D = 0000 0000 0000 0000 0000（20位）
        int[] C = SubIntArray(y, yStart, yStart + n2 - 1);
        int[] D = SubIntArray(y, yStart + n2, y.length - 1);


        /*演算步骤（这里考虑一般性）：
                     （x = A * 2^n1 + B, y = C * 2^n2 + D
        xy = ac*2(n1+n2)+bd+ad*2^n1+bc*2^n2
        xy=ac*2(n1 + n2) + bd + (ad+bc)*2^n1 + bc*(2^n2-2^n1)
        xy=ac*2^(n1 + n2) + bd + ((a+b)(c+d) - ac - bd)*2^n1 + bc*(2^n2-2^n1)
        xy = ac*2^(n1 + n2) + bd + (a+b)(c+d) * 2^n1 - ac*2^n1 - bd*2^n1 + bc*(2^n2-2^n1)）*/

        //**递归求解过程，演算上面的表达式**//
        int[] AC = BigIntegerMultiplication(A, C);
        int[] BD = BigIntegerMultiplication(B, D);
        int[] BC = BigIntegerMultiplication(B, C);

        int[] AaddB = BinaryAdd(A, B);
        int[] CaddD = BinaryAdd(C, D);

        int[] AaddBCaddD = BigIntegerMultiplication(AaddB, CaddD);

        //处理奇数问题，这里我的A = 0000 0000 0000 0000 0010
        //事实上，此时的x = A * 2^n1[向上取整] + B，大家可以自己推演一下，结合前面部分的理论的那张图，就知道了。
        n1 = xLength % 2 == 1 ? xLength / 2 + 1 : xLength / 2;
        n2 = yLength % 2 == 1 ? yLength / 2 + 1 : yLength / 2;
        //一顿演算
        int[] AddPart = BinaryAdd(BinaryAdd(BinaryAdd(MoveLeft(AC, (n1 + n2)), BD), MoveLeft(AaddBCaddD, n1)), MoveLeft(BC, n2));
        int[] XY = BinarySub(BinarySub(BinarySub(AddPart, MoveLeft(AC, n1)), MoveLeft(BD, n1)),MoveLeft(BC,n1));

        //返回结果
        return XY;//XY
    }

    //数组左移a位
    public static int[] MoveLeft(int[] A, int a)
    {
        int[] B = new int[MaxLength];
        //获得有效数组，因为前面我的ABCD长度都直接设置为MaxLength了，但是这就很蠢了，根本就没有必要---
        int[] EffectiveA = SubIntArray(A, A.length - GetEffctiveLength(A), A.length - 1, true);
        for (int i = EffectiveA.length - 1; i >= 0; i--)
        {
            B[MaxLength - a + i - EffectiveA.length] = EffectiveA[i];
        }
        return B;
    }
    //二进制加法
    //这算是统一长度之后的好处吧，就是加法特别简单
    public static int[] BinaryAdd(int[]A,int[] B)
    {

        for(int i = MaxLength - 1; i >= 0; i--)
        {
            if(A[i] + B[i] == 2)
            {
                A[i] = 0;
                A[i - 1]++;
            }

            else if(A[i] + B[i] == 3)
            {
                A[i] = 1;
                A[i - 1]++;
            }

            else
            {
                A[i] += B[i];
            }
        }
        return A;
    }

    //二进制减法
    //和加法类似
    public static int[] BinarySub(int[] A, int[] B)
    {
        for (int i = MaxLength - 1; i >= 0; i--)
        {
            if (A[i] - B[i] == -1)
            {
                A[i] = 1;
                A[i - 1]--;
            }
            else if(A[i] - B[i] == -2)
            {
                A[i] = 0;
                A[i - 1]--;
            }
            else
            {
                A[i] -= B[i];
            }
        }
        return A;
    }


    //打印结果
    public static void GetResult(int[] A)
    {
        int startWith = -1;
        for(int i = 0; i < A.length; i++)
        {
            if (A[i] != 0)
            {
                startWith = i;
                break;
            }
        }
        if(startWith == -1)
        {
            System.out.print("0");
        }
        else
        {
            for(int i = startWith; i < A.length; i++)
            {
                System.out.print(A[i]);
                System.out.print(" ");
            }
        }
        System.out.print(" ");
    }

    //获取二进制数组的有效长度
    public static int GetEffctiveLength(int[] A)
    {
        int startWith = -1;
        for (int i = 0; i < A.length; i++)
        {
            if (A[i] != 0)
            {
                startWith = i;
                break;
            }
        }
        if (startWith == -1)
        {
            return 1;
        }
        else
        {
            return A.length - startWith;
        }
    }

    //获取二进制数组有效长度的开始的下标
    public static int GetStartWith(int[] A)
    {
        int startWith = -1;
        for (int i = 0; i < A.length; i++)
        {
            if (A[i] != 0)
            {
                startWith = i;
                break;
            }
        }
        return startWith;
    }


    //计算朴素乘法，事实上a的值只可能为0或者1，因此，直接判断就好了
    public static int[] SimpleMulti(int[] A, int a)
    {
        int[] B = new int[MaxLength];
        if (a == 0)
        {
            for (int i = 0; i < A.length; i++)
            {
                B[MaxLength - i - 1] = 0;
            }
        }
        else
        {
            for (int i = 0; i < A.length; i++)
            {
                B[MaxLength - i - 1] = A[A.length - i - 1];
            }
        }
        return B;
    }

    //获取统一长度的子数组
    public static int[] SubIntArray(int[] A, int p, int q)
    {
        int[] B = new int[MaxLength];
        for (int i = q; i >= p; i--)
        {
            B[MaxLength - 1 + (i - q)] = A[i];
        }
        return B;
    }

    //重载SubIntArray，获取有效长度的子数组
    //大概只用到了一个地方（左移MoveLeft的时候）
    public static int[] SubIntArray(int[] A, int p, int q, boolean isCutTrue)
    {
        int[] B = new int[GetEffctiveLength(A)];
        if(GetEffctiveLength(A) == 1)
        {
            B[B.length-1] = A[A.length-1];
        }
        else
        {
            for (int i = q; i >= p; i--)
            {
                B[GetEffctiveLength(A) + (i - q) - 1] = A[i];
            }
        }
        return B;
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        long start = System.currentTimeMillis();

        InputInteger();
        int[] XY=BigIntegerMultiplication(X, Y);
        GetResult(XY);

        long end = System.currentTimeMillis();
        System.out.println();
        System.out.println("程序运行时间："+(end-start)+"ms");
    }
}
