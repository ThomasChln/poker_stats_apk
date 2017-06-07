package com.example.pok_api7to17;
import java.util.Arrays;


public class Rules
{	
	public Rules()
	{
	}
	
	public int hig(int[] copy)
	{
		int n = copy[0];
		for (int i = 0; i < 4; i++)
			n = (n * (12 - i) + copy[i + 1]);

		return n;
	}
	
	public int pai(int[] copy)
	{
		int res;
		for (int i = 0; i != 6; i++)
			if (copy[i] == copy[i + 1])
			{
				res = copy[i];

				int j;
				for (j = 0; j != i && j != 3; j++) 
					res = res * 12 + copy[j];
				
				for (; j != 3; j++)
					res = res * 12 + copy[j + 2];
				
				return res;	
			}
		return -1;
	}
	
	public int dpa(int[] copy)
	{
		for (int i = 0; i < 4; i++)
			if (copy[i] == copy[i + 1])
			{
				for (int j = i; j < 6; j++)
					if ((i != j) && (copy[j] == copy[j + 1]))
					{
						int k;
						if (i == 0)
						{
							if (j == 2)
							{
								k = 4;
								return ((copy[i] * 12 + copy[j]) * 11 + copy[k]);
							}
							k = 2;
							return ((copy[i] * 12 + copy[j]) * 11 + copy[k]);
						}
						k = 0;
						return ((copy[i] * 12 + copy[j]) * 11 + copy[k]);
					}
				return -1;
			}
		return -1;
	}	

	public int bre(int[] copy)
	{
		for (int i = 0; i < 5; i++)
		{
			int j;
			for (j = i + 1; j < i + 3; j++)
				if (copy[i] != copy[j])
					j = i + 3;
			if (j == i + 3)
			{
				int k;
				if (i == 0)
				{
					j = 3;
					k = 4;
					return ((copy[i] * 12 + copy[j]) * 11 + copy[k]);
				}
				j = 0;
				if (i == 1)
				{
					k = 4;
					return ((copy[i] * 12 + copy[j]) * 11 + copy[k]);
				}
				k = 1;
				return ((copy[i] * 12 + copy[j]) * 11 + copy[k]);
			}
		}
		return -1;
	}
	
	public int ful(int[] copy)
	{
		for (int i = 0; i < 5; i++)
		{
			int j;
			for (j = i + 1; j < i + 3; j++)
				if (copy[i] != copy[j])
					j = i + 3;
			if (j == i + 3)
			{
				for (j = 0; j < 6; j++)
					if ((copy[j] != copy[i]) && (copy[j] == copy[j + 1]))
						return (copy[i] * 12 + copy[j]);
				return -1;
			}
		}
		return -1;
	}
	
	public int car(int[] copy)
	{
		for (int i = 0; i < 4; i++)
		{
			int j;
			for (j = i + 1; j < i + 4; j++)
				if (copy[i] != copy[j])
					j = i + 4;
			if (j == i + 4)
			{
				if (i == 0)
					return (copy[i] * 12 + copy[4]);

				return (copy[i] * 12 + copy[0]);
			}
		}
		return -1;
	}

	public int col(int[] game)
	{
		for (int i = 0; i < 3; i++)
		{
			int n = game[i] / 100;
			int j;
			for (j = i + 1; j < i + 5; j++)
				if (n != game[j] / 100)
					j = i + 5;

			if (j == i + 5)
			{
				for (n = game[i] % 100; (j - i) > 1; j--)
					n = (n * (12 - (5 - (j - i))) + game[i + (6 - (j - i))] % 100);
				return n;
			}
		}
		return -1;
	}
	
	public int qf(int[] copy)
	{			
		int[] tmpcop;
		int cnt;
		int n = 0;
		for (int i = 0; i < copy.length; i++)
			if (copy[i] % 100 == 0)
				n++;
		if (n != 0)
		{
			cnt = n;
			tmpcop = new int[copy.length + n];
			for (int j=0; j < copy.length; j++)
				tmpcop[j] = copy[j];
			for (int j = 0; cnt != 0; j++)
				if (copy[j] % 100 == 0)
				{
					tmpcop[tmpcop.length - cnt] = copy[j] + 13;
					cnt--;
				}
			copy = tmpcop;
			Arrays.sort(copy);
		}

		for (int i = 0; i < 3 + n; i++)
		{
			cnt = 0;
			int j;
			for (j = i + 1; j < i + 5; j++)
			{
				while ((j + cnt < 6) && (copy[j + cnt] == copy[j + cnt + 1]))
					cnt++;
				if ((copy[i] + (j - i) != copy[j + cnt])
				//FIXME?
					|| ((j + cnt == 6) && (j != i + 4)))
					j = i + 5;
			}
			if (j == i + 5)
				return copy[i];
		}
		return -1;
	}
}
