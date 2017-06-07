package com.example.pok_api7to17;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class Deck {
	boolean getSups, getEqs, getInfs;
	List<List<Integer>> games;
	List<Integer> board, stack;
	int[] suppoints, eqpoints, infpoints;
	int width, height, iter_size, simu_size, max = 0;
	float supMean, supStdDev, eqMean, eqStdDev,infMean,infStdDev;
	List<Float> sups, eqs, infs;
	Rules r;
	int gamelen = 7;
	boolean maxInSups=false,maxInEqs=false;
	public Deck(List<List<Integer>> g, List<Integer> b, List<Integer> d,
			int width, int height, int iter_size, int simu_size,
			List<Float> sups, List<Float> eqs, List<Float> infs, boolean[] get_bools) {
		this.width = width;
		this.height = height;
		getSups = get_bools[0];
		getEqs = get_bools[1];
		getInfs = get_bools[2];
		if (getSups) {
			this.suppoints = new int[width * 2];
		}
		if (getEqs) {
			this.eqpoints = new int[width * 2];
		}
		if (getInfs) {
			this.infpoints = new int[width * 2];
		}
		this.iter_size = iter_size;
		this.simu_size = simu_size;
		this.sups = sups;
		this.eqs = eqs;
		this.infs = infs;
		stack = new ArrayList<Integer>();
		for (int i = 0; i <= 3; i++) {
			for (int j = 0; j <= 12; j++) {
				stack.add(i * 100 + j);
			}
		}
		if (g != null) {
			for (List<Integer> o : g) {
				for (Integer x : o) {
					stack.remove(x);
				}
			}
		}
		if (b != null){
			for (Integer x : b) {
				stack.remove(x);
			}
		}
		else {
			b = new ArrayList<Integer>();
		}
		
		if (d != null) {
			for (Integer x : d) {
				stack.remove(x);
			}
		}
		
		games = g;
		board = b;
		r = new Rules();
		mc_gene();
	}
	
	public void mc_gene() {
		int[] gamevals = new int[games.size()];
		float eq, sup;
		int n, i, tmp, tmpn, tmpm;
		List<Integer> tmpboard = new ArrayList<Integer>(), tmpgame = new ArrayList<Integer>(), tmpdiscard = new ArrayList<Integer>();
		for (int j = 0; j < iter_size; j++) {
			for (n = 0, sup = 0, eq = 0; n < simu_size; n++) {
				while (tmpboard.size() != 5 - board.size()) {
					while (tmpboard.contains((tmp = stack.get((int) (Math.random() * stack.size())))))
						;
					tmpboard.add(tmp);
				}
				tmpdiscard.addAll(tmpboard);
				tmpboard.addAll(board);
				i = 0;
				for (List<Integer> o : games) {
					while (tmpgame.size() != 2 - o.size()) {
						while (tmpdiscard.contains(tmp = stack.get((int) (Math.random() * stack.size()))))
							;
						tmpgame.add(tmp);
						tmpdiscard.add(tmp);
					}
					tmpgame.addAll(o);
					tmpboard.addAll(tmpgame);
					gamevals[i++] = check(tmpboard);
					tmpboard.removeAll(tmpgame);
					tmpgame.clear();
				}
				tmpn = gamevals[0] % 10;
				tmpm = gamevals[0] / 10;
				for (i = 1; i < gamevals.length; i++) {
					if (tmpn > gamevals[i] % 10
							|| (tmpn == gamevals[i] % 10 && tmpm > gamevals[i] / 10)) {
						i = gamevals.length;
					}
				}
				if (i == gamevals.length) {
					for (i = 1; i < gamevals.length; i++) {
						if (tmpn == gamevals[i] % 10 && tmpm == gamevals[i] / 10) {
							eq++;
							i = gamevals.length;
						}
					}
					if (i == gamevals.length) {
						sup++;
					}
				}
				tmpdiscard.clear();
				tmpboard.clear();
			}
			sups.add(sup / (float) simu_size);
			eqs.add(eq / (float) simu_size);
			infs.add((simu_size - eq - sup)  / (float) simu_size);
		}
		int[] pts=null,ptseq=null,ptsinf=null;
		if (getSups) {
			pts = new int[width];
			for (Float x : sups) {
				++pts[(int) (x * (width - 1))];
				if (pts[(int) (x * (width - 1))] > max) {
					max = pts[(int)(x * (width - 1))];
					maxInSups = true;
					maxInEqs = false;
				}
			}
		}
		if (getEqs) {
			ptseq = new int[width];
			for (Float x : eqs) {
				++ptseq[(int) (x * (width - 1))];
				if (ptseq[(int) (x * (width - 1))] > max) {
					max = ptseq[(int)(x*(width - 1))];
					maxInSups = false;
					maxInEqs = true;
				}
			}
		}
		if (getInfs) {
			ptsinf = new int[width];
			for (Float x : infs) {
				++ptsinf[(int) (x * (width - 1))];
				if (ptsinf[(int) (x * (width - 1))] > max) {
					max = ptsinf[(int)(x*(width - 1))];
					maxInSups = false;
					maxInEqs = false;
				}
			}
		}
		supMean = eqMean = supStdDev = eqStdDev = infMean = infStdDev = 0;
		for (int i1 = 0; ((getSups && (i1 < pts.length * 2))
				|| (getEqs && (i1 < ptseq.length * 2))
				|| (getInfs && (i1 < ptsinf.length * 2))); i1=i1+2) {
			if (getSups) {
				suppoints[i1] = i1 / 2;
				suppoints[i1 + 1] = (int) (height - pts[i1 / 2] * (height / (float) max));
				if (pts[i1 / 2] != 0) {
					supMean += i1 * pts[i1 / 2] * 50 / (float) width;
				}
			}
			if (getEqs) {
				eqpoints[i1] = i1 / 2;
				eqpoints[i1+1] = (int) (height - ptseq[i1/2] * (height / (float) max));
				if (ptseq[i1 / 2] != 0) {
					eqMean += i1 * ptseq[i1 / 2] * 50 / (float) width;
				}
			}
			if (getInfs) {
				infpoints[i1] = i1 / 2;
				infpoints[i1+1] = (int) (height - ptsinf[i1/2] * (height / (float) max));
				if (ptsinf[i1 / 2] != 0) {
					infMean += i1 * ptsinf[i1 / 2] * 50 / (float) width;
				}
			}
		}
		if (getSups) {
			supMean /= sups.size();
			for (Float f : sups) {
				supStdDev += Math.pow(f * 100 - supMean, 2);
			}
			supStdDev = (float) Math.sqrt(supStdDev / (sups.size() - 1));
		}
		if (getEqs) {
			eqMean /= eqs.size();
			for (Float f : eqs) {
				eqStdDev += Math.pow(f * 100 - eqMean, 2);
			}
			eqStdDev = (float) Math.sqrt(eqStdDev / (eqs.size() - 1));
		}
		if (getInfs) {
			infMean /= infs.size();
			for (Float f : infs) {
				infStdDev += Math.pow(f * 100 - infMean, 2);
			}
			infStdDev = (float) Math.sqrt(infStdDev / (infs.size() - 1));
		}
	}
	public int[] getSupPoints() {
		return suppoints;
	}
	public int[] getEqPoints() {
		return eqpoints;
	}
	public int[] getInfPoints() {
		return infpoints;
	}
	public void r_gene(List<Integer> game, List<Integer> deck) {
		int i = 0;
		if (game.size() == gamelen) {
			check(game);
		} else if (deck.size() == gamelen - game.size()) {
			for (i = 0; i < game.size(); i++) {
				deck.add(game.get(i));
			}
			check(deck);
		}
		else {
			List<Integer> cg = new ArrayList<Integer>();
			List<Integer> cd = new ArrayList<Integer>();
			for (i = 0; i < game.size(); i++) {
				cg.add(game.get(i));
			}
			game.add(deck.remove(0));
			for (i = 0; i < deck.size(); i++) {
				cd.add(deck.get(i));
			}
			r_gene(game, deck);
			r_gene(cg, cd);
		}
	}
	public int check(List<Integer> game) {
		int i, tmp;
		int[] copywc, copy;
		tmp = 7;
		copy = new int[tmp];
		for (i = 0; i < tmp; i++) {
			copy[i] = game.get(i);
		}
		Arrays.sort(copy);
		if ((tmp = r.qf(copy)) != -1) {
			return tmp * 10;	
		}
		
		tmp = 7;
		copywc = new int[tmp];
		for (i = 0; i < tmp; i++) {
			copywc[i] = copy[i] % 100;
		}
		Arrays.sort(copywc);
		
		if ((tmp = r.car(copywc)) != -1) {
			return (tmp * 10 + 1);
		}
		if ((tmp = r.ful(copywc)) != -1) {
			return (tmp * 10 + 2);
		}
		if ((tmp = r.col(copy)) != -1) {
			return (tmp * 10 + 3);
		}
		if ((tmp = r.qf(copywc)) != -1) {
			return (tmp * 10 + 4);
		}
		if ((tmp = r.bre(copywc)) != -1) {
			return (tmp * 10 + 5);
		}
		if ((tmp = r.dpa(copywc)) != -1) {
			return (tmp * 10 + 6);
		}
		if ((tmp = r.pai(copywc)) != -1) {
			return (tmp * 10 + 7);
		}
		tmp = r.hig(copywc);
		return (tmp * 10 + 8);
	}
}