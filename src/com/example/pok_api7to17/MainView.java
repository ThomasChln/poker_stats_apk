package com.example.pok_api7to17;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
public class MainView extends View {
	boolean next, getSups=true, getEqs=true, getInfs=true, launched=false;
	int cur_gv, width, height, simu_size, iter_num=-1, iter_batch=20, iter_size=3,
			card_size_width=56, card_size_height=78, widthMarginLeft=30,
			widthMarginRight=5, heightMarginUp=5, heightMarginDown=80;
	Rect sizes, button_launch, button_options, button_board, button_discard, button_players;
	Paint pa;
	Deck d;
	Handler handler = new Handler();
	ListView lv;
	ArrayList<GridView> gvs;
	List<Float> sups, eqs, infs;
	ArrayList<Integer> mSelectedItems;
	AlertDialog optAd, playAd, boardAd, discardAd;
	ArrayList<AlertDialog> playAds;
	Map<Integer, Integer> cards1, currentcardspos;
	ArrayList<ArrayList<Integer>> selected;
	List<List<Integer>> selectedCards;
	public MainView(Context context) {
		super(context);
		sups = new ArrayList<Float>();
		eqs = new ArrayList<Float>();
		infs = new ArrayList<Float>();
		selected = new ArrayList<ArrayList<Integer>>();
		mSelectedItems = new ArrayList<Integer>();
		optAd = buildOptionsDialog(context);
		playAd = null;
		boardAd = null;
		discardAd = null;
		initCards();
		next = true;
		pa = new Paint();
		sizes = new Rect();
	}
	private Runnable timedTask = new Runnable() {
		@Override
		public void run() {
			if (iter_num > iter_batch) {
				handler.removeCallbacks(timedTask);
				launched = false;
				iter_num = 1;
				invalidate();
				return;
			}
			invalidate();
			handler.post(timedTask);
		}
	};
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (x > button_launch.left && x < button_launch.right
					&& y > button_launch.top && y < button_launch.bottom) {
				if (launched) {
					handler.removeCallbacks(timedTask);
					launched = false;
					iter_num = 1;
					invalidate();
					return true;
				}
				handler.post(timedTask);
				launched = true;
				invalidate();
			}
			else if (x > button_options.left && x < button_options.right
					&& y > button_options.top && y < button_options.bottom) {
				handler.removeCallbacks(timedTask);
				launched = false;
				if (iter_num > 1) iter_num = 1;
				optAd.show();
			}
			else if (x > button_players.left && x < button_players.right
					&& y > button_players.top && y < button_players.bottom) {
				playAd.show();
			}
			else if (x > button_board.left && x < button_board.right
					&& y > button_board.top && y < button_board.bottom) {
				cur_gv = 0; 
				gvs.get(cur_gv).setAdapter(new MyAdapter(getContext()));
				boardAd.show();
			}
			else if (x > button_discard.left && x < button_discard.right
					&& y > button_discard.top && y < button_discard.bottom) {
				cur_gv = 1;
				gvs.get(cur_gv).setAdapter(new MyAdapter(getContext()));
				discardAd.show();
			}
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
		}
		return true;
	}
	AlertDialog.Builder getDefaultAlertDialogBuilder(Context context) {
		AlertDialog.Builder tmpBuild = new AlertDialog.Builder(context);
		tmpBuild.setNegativeButton("Back", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		return tmpBuild;
	}
	class MyListAdapter extends BaseAdapter {
		ArrayList<Button> buttons;
		public MyListAdapter(Context context) {
			Button bu;
			String[] strs = new String[] {"Add Player", "Delete Player", "Player 1", "Player 2"};
			buttons = new ArrayList<Button>();
			for (int i = 0; i < strs.length; i++){
				bu = new Button(context);
				bu.setText(strs[i]);
				if (i == 0) {
					bu.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (selected.size() < 10) {
								selected.add(new ArrayList<Integer>());
								Button bu = new Button(getContext());
								bu.setId(selected.size() - 1);
								bu.setText("Player " + (selected.size() - 2)); 
								bu.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										cur_gv = v.getId();
										cardListener(v);
									}
								});
								buttons.add(bu);
								lv.setAdapter(new MyListAdapter(buttons));
								iter_num = 0;
							}
						}
					});
				} else if (i == 1) {
					bu.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (selected.size() > 4) {
								selected.remove(selected.size() - 1);
								buttons.remove(selected.size());
								lv.setAdapter(new MyListAdapter(buttons));
								iter_num = 0;
							}
						}
					});
				} else if (i == 2) {
					selected.add(new ArrayList<Integer>());
					bu.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							cur_gv = 2;
							cardListener(v);
						}
					});
				} else if (i == 3) {
					selected.add(new ArrayList<Integer>());
					bu.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							cur_gv = 3;
							cardListener(v);
						}
					});
				}
				buttons.add(bu);
			}
		}
		public MyListAdapter(ArrayList<Button> buttons) {
			this.buttons = buttons;
		}
		@Override
		public int getCount() {
			return buttons.size();
		}
		@Override
		public Object getItem(int arg0) {
			return buttons.get(arg0);
		}
		@Override
		public long getItemId(int arg0) {
			return 0;
		}
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			return buttons.get(arg0);
		}
	}
	public void cardListener(View v) {
		if (playAds.size() <= cur_gv - 2) {
			for (int i = playAds.size() ; i < cur_gv - 1; i++) {
				AlertDialog.Builder tmp = new AlertDialog.Builder(getContext());
				GridView tmpgv = new GridView(getContext());
				tmpgv.setNumColumns(width / card_size_width);
				gvs.add(i + 2, tmpgv);
				tmp.setView(gvs.get(i + 2));
				tmp.setTitle("Player " + (i + 1));
				tmp.setNegativeButton("Back", null);
				tmp.setPositiveButton("Players", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						playAd.show();
					}
				});
				playAds.add(tmp.create());
			}
		}
		gvs.get(cur_gv).setAdapter(new MyAdapter(getContext()));
		playAd.dismiss();
		playAds.get(cur_gv - 2).show();		
	}
	void buildCardsDialog(Context context) {
		String[] strs = new String[] {"Board", "Discard", "Players"};
		gvs = new ArrayList<GridView>();
		for (int i = 0; i < 2; i++) {
			selected.add(new ArrayList<Integer>());
		}
		playAds = new ArrayList<AlertDialog>();
		for (int i = 0; i < 3; i++) {
			AlertDialog.Builder tmpAdBuild = getDefaultAlertDialogBuilder(context);
			tmpAdBuild.setTitle(strs[i]);
			if (i == 2) {
				lv = new ListView(context);
				lv.setAdapter(new MyListAdapter(context));
				tmpAdBuild.setView(lv);
				playAd = tmpAdBuild.create();
			} else {
				gvs.add(new GridView(context));
				gvs.get(i).setNumColumns(width / card_size_width);
				tmpAdBuild.setView(gvs.get(i));
				if (i == 0) {
					boardAd = tmpAdBuild.create();
				} else {
					discardAd = tmpAdBuild.create();
				}
			}
		}
	}
	ImageButton getDefaultButton(Context context) {
		ImageButton ib = new ImageButton(context);
		ib.setAdjustViewBounds(true);
		ib.setMaxHeight(card_size_height);
		return ib;
	}
	ImageButton getCardButton(Context context, int card, int resid) {
		ImageButton ib = getDefaultButton(context);
		ib.setImageResource(resid);
		ib.setId(card);
		return ib;
	}
	ImageButton getBackCardButton(Context context) {
		ImageButton ib = getDefaultButton(context);
		ib.setImageResource(R.drawable.back);
		return ib;
	}
	class MyAdapter extends BaseAdapter {
		Context mContext;
		ArrayList<ImageButton> images, selectedImgs;
		OnClickListener my_listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int tmpId = v.getId();
				if (tmpId != -1) {
					boolean cur_contains = selected.get(cur_gv).contains(tmpId);
					if (!cur_contains
							&& (cur_gv != 0 || selected.get(0).size() < 5)
							&& (cur_gv < 2 || selected.get(cur_gv).size() < 2)) {
						selected.get(cur_gv).add(tmpId);
						images.add(images.lastIndexOf(v), getBackCardButton(mContext));
						images.remove((ImageButton) v);
						images.add((ImageButton) v);
						gvs.get(cur_gv).setAdapter(new MyAdapter(mContext, images));
						iter_num = 0;
						return;
					} 
					if (cur_contains) {
						int tmpPos = currentcardspos.get(tmpId);
						images.remove(v);
						images.add(tmpPos, (ImageButton) v);
						images.remove(tmpPos + 1);
						selected.get(cur_gv).remove((Object) tmpId);
						gvs.get(cur_gv).setAdapter(new MyAdapter(mContext, images));
						iter_num = 0;
					}
				}
			}
		};
		MyAdapter(Context context) {
			this.images = new ArrayList<ImageButton>();
			Object[] cards = cards1.keySet().toArray();
			currentcardspos = new TreeMap<Integer, Integer>();
			ImageButton ib;
			int[] tmpSelected = new int[selected.get(cur_gv).size()];
			int backcards_for_newline = (width / card_size_width) - 52 % (width / card_size_width);
			if (backcards_for_newline == (width / card_size_width)) {
				backcards_for_newline = 0;
			}
			for (int i = cards.length - 1, j = 0; i >= - backcards_for_newline - tmpSelected.length; i--) {
				if (i >= 0) {
					int cardval = cards1.get(cards[i]);
					currentcardspos.put(cardval, cards.length - 1 - i);
					boolean contains = false;
					for (ArrayList<Integer> k : selected) {
						if (k.contains(cardval)) {
							contains = true;
						}
					}
					if (contains) { 
						ib = getBackCardButton(context);
						if (selected.get(cur_gv).contains(cardval))	{
							tmpSelected[j] = i;
							j++;
						}
					} else {
						ib = getCardButton(context, cardval, (Integer) cards[i]);
					}
					ib.setOnClickListener(my_listener);
				} else if (i >= - backcards_for_newline) {
					ib = getBackCardButton(context);
				} else {
					ib = getCardButton(context, cards1.get(cards[tmpSelected[tmpSelected.length - j]]), (Integer) cards[tmpSelected[tmpSelected.length - j]]);
					ib.setOnClickListener(my_listener);
					j--;
				}
				images.add(ib);
			}
			mContext = context;
		}
		MyAdapter(Context context, ArrayList<ImageButton> ima) {
			images = ima;
			mContext = context;
		}
		@Override
		public int getCount() {
			return images.size();
		}
		@Override
		public Object getItem(int position) {
			return images.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return images.get(position);
		}
	}
	AlertDialog buildOptionsDialog(Context context) {
		AlertDialog.Builder optionsDialog = new AlertDialog.Builder(context);
		optionsDialog.setTitle("Options");
		if (getSups) {
			mSelectedItems.add(0);
		}
		if (getEqs) {
			mSelectedItems.add(1);
		}
		if (getInfs) {
			mSelectedItems.add(2);
		}
		optionsDialog.setMultiChoiceItems(new CharSequence[] {"Wins", "Draws", "Defeats"},
				new boolean [] {getSups, getEqs, getInfs},
				new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if (isChecked) {
					mSelectedItems.add(which);
				} else if (mSelectedItems.contains(which)) {
					mSelectedItems.remove(Integer.valueOf(which));
				}
			}
		});
		optionsDialog.setPositiveButton("Back", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getSups = mSelectedItems.contains(0);
				getEqs = mSelectedItems.contains(1);
				getInfs = mSelectedItems.contains(2);
			}
		});

		return optionsDialog.create();
	}
	protected Path getPath(int[] pts) {
		Path tmppath = new Path();
		tmppath.moveTo(widthMarginLeft, height - heightMarginDown);
		for(int i=0; i<pts.length; i=i+2) {
			tmppath.moveTo(pts[i] + widthMarginLeft, height - heightMarginDown);
			tmppath.lineTo(pts[i] + widthMarginLeft, pts[i+1] + heightMarginUp);
		}
		tmppath.close();
		return tmppath;	
	}
	protected void mainPok (int width, int height) {
		if (iter_num == 1) {
			selectedCards = new ArrayList<List<Integer>>();
			for (int i = 2; i < selected.size(); i++)	{
				selectedCards.add(selected.get(i));
			}
		}
		d = new Deck(selectedCards, selected.get(0), selected.get(1), width, height, iter_size, simu_size, sups, eqs, infs, new boolean[] {getSups, getEqs, getInfs});
	}
	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
		iter_num++;
		if (iter_num == 0) {
			canvas.getClipBounds(sizes);
			height = sizes.bottom;
			width = sizes.right;
			button_launch = new Rect(width - 90, height - 30, width - 10, height - 3);
			button_options = new Rect(width - 90, height - 60, width - 10, height - 33);
			button_players = new Rect(width - 194, height - 60, width - 94, height - 43);
			button_board = new Rect(width - 194, height - 40, width - 94, height - 23);
			button_discard = new Rect(width - 194, height - 20, width - 94, height - 3);
			simu_size = width - widthMarginRight - widthMarginLeft;
			pa.setStyle(Paint.Style.STROKE);
			pa.setTextSize(13);
			buildCardsDialog(getContext());
		}
		pa.setColor(Color.GRAY);
		canvas.drawRect(button_launch, pa);
		if (!launched) {
			canvas.drawText("Launch", button_launch.left + 20, button_launch.top + 20, pa);
		} else {
			canvas.drawText("Stop", button_launch.left + 26, button_launch.top + 20, pa);
		}
		canvas.drawRect(button_options, pa);
		canvas.drawText("Options", button_options.left + 20, button_options.top + 20, pa);
		canvas.drawRect(button_players, pa);
		canvas.drawText("Players", button_players.left + 30, button_players.top + 13, pa);
		canvas.drawRect(button_board, pa);
		canvas.drawText("Board", button_board.left + 33, button_board.top + 13, pa);
		canvas.drawRect(button_discard, pa);
		canvas.drawText("Discard", button_discard.left + 30, button_discard.top + 13, pa);
		canvas.drawRect(widthMarginLeft, heightMarginUp, width - widthMarginRight, height - heightMarginDown, pa);
		canvas.drawLine(widthMarginLeft + (width - widthMarginRight - widthMarginLeft) / 2, heightMarginUp, widthMarginLeft + (width - widthMarginRight - widthMarginLeft) / 2, height - heightMarginDown, pa);
		canvas.drawLine(widthMarginLeft, heightMarginUp + (height - heightMarginDown - heightMarginUp) / 2, width - widthMarginRight, heightMarginUp + (height - heightMarginDown - heightMarginUp) / 2, pa);
		canvas.drawText("0%", widthMarginLeft, 13 + height - heightMarginDown, pa);
		canvas.drawText("50%", widthMarginLeft + (width - widthMarginLeft - widthMarginRight) / 2 - 11, 13 + height - heightMarginDown, pa);
		canvas.drawText("100%", width - widthMarginRight - 36, 13 + height - heightMarginDown, pa);
		canvas.drawLine(0, 16 + height - heightMarginDown, width, 16 + height - heightMarginDown, pa);
		if (iter_num == 0) {
			return;
		} else if (iter_num == 1) {
			sups.clear();
			eqs.clear();
			infs.clear();
		}
		double time_diff = SystemClock.currentThreadTimeMillis();
		mainPok((width - widthMarginRight - widthMarginLeft), (height - heightMarginUp - heightMarginDown));
		time_diff = (SystemClock.currentThreadTimeMillis() - time_diff) / 1000;
		String str, str1;
		if (getEqs) {
			pa.setColor(Color.BLUE);
			canvas.drawPath(getPath(d.getEqPoints()), pa);
			str = Float.toString(d.eqMean);
			if (str.substring(str.indexOf('.')).length() > 4) {
				str = str.substring(0, str.indexOf('.') + 4);
			}
			str1 = Float.toString(d.eqStdDev);
			if (str1.substring(str1.indexOf('.')).length() > 4) {
				str1 = str1.substring(0, str1.indexOf('.') + 4);
			}
			canvas.drawText("Draws: " + str + "% +- " + str1 + "%", widthMarginRight, height - heightMarginDown + 35, pa);
		}
		if (getInfs) {
			pa.setColor(Color.RED);
			canvas.drawPath(getPath(d.getInfPoints()), pa);
			str = Float.toString(d.infMean);
			if (str.substring(str.indexOf('.')).length() > 4) {
				str = str.substring(0, str.indexOf('.') + 4);
			}
			str1 = Float.toString(d.infStdDev);
			if (str1.substring(str1.indexOf('.')).length() > 4) {
				str1 = str1.substring(0, str1.indexOf('.') + 4);
			}
			canvas.drawText("Defeats: " + str + "% +- " + str1 + "%", widthMarginRight, height - heightMarginDown + 35 + ((getEqs) ? 20 : 0), pa);			
		}
		if (getSups) {
			pa.setColor(Color.GREEN);
			canvas.drawPath(getPath(d.getSupPoints()), pa);
			str = Float.toString(d.supMean);
			if (str.substring(str.indexOf('.')).length() > 4) {
				str = str.substring(0, str.indexOf('.') + 4);
			}
			str1 = Float.toString(d.supStdDev);
			if (str1.substring(str1.indexOf('.')).length() > 4) {
				str1 = str1.substring(0, str1.indexOf('.') + 4);
			}
			canvas.drawText("Wins: " + str + "% +- " + str1 + "%", widthMarginRight, height - heightMarginDown + 35 + ((getInfs) ? 20 : 0) + ((getEqs) ? 20 : 0), pa);
		}
		int scaleUi;
		if (d.maxInSups) {
			scaleUi = d.max * 100 / sups.size();
		} else if (d.maxInEqs) {
			scaleUi = d.max * 100 / eqs.size();
		} else {
			scaleUi = d.max * 100 / infs.size();
		}
		pa.setColor(Color.GRAY);
		int games_per_sec = ((int) (iter_size * simu_size / time_diff)) * (selected.size() - 2);
		canvas.drawText("Games/seconds: " + ((games_per_sec >= 10000) ? ((games_per_sec / 1000) + " k") : games_per_sec),
				width - 135 - widthMarginRight, 15 + heightMarginUp, pa);
		canvas.drawText(Integer.toString(scaleUi) + "%", 3, 25, pa);
		canvas.drawText(Integer.toString(scaleUi / 2) + "%", 3, 9 + (height - heightMarginDown) / 2, pa);
	}
	void initCards() {
		cards1 = new LinkedHashMap<Integer, Integer>();
		cards1.put(R.drawable.c2c, 312);
		cards1.put(R.drawable.c2d, 212);
		cards1.put(R.drawable.c2h, 112);
		cards1.put(R.drawable.c2s, 12);
		cards1.put(R.drawable.c3c, 311);
		cards1.put(R.drawable.c3d, 211);
		cards1.put(R.drawable.c3h, 111);
		cards1.put(R.drawable.c3s, 11);
		cards1.put(R.drawable.c4c, 310);
		cards1.put(R.drawable.c4d, 210);
		cards1.put(R.drawable.c4h, 110);
		cards1.put(R.drawable.c4s, 10);
		cards1.put(R.drawable.c5c, 309);
		cards1.put(R.drawable.c5d, 209);
		cards1.put(R.drawable.c5h, 109);
		cards1.put(R.drawable.c5s, 9);
		cards1.put(R.drawable.c6c, 308);
		cards1.put(R.drawable.c6d, 208);
		cards1.put(R.drawable.c6h, 108);
		cards1.put(R.drawable.c6s, 8);
		cards1.put(R.drawable.c7c, 307);
		cards1.put(R.drawable.c7d, 207);
		cards1.put(R.drawable.c7h, 107);
		cards1.put(R.drawable.c7s, 7);
		cards1.put(R.drawable.c8c, 306);
		cards1.put(R.drawable.c8d, 206);
		cards1.put(R.drawable.c8h, 106);
		cards1.put(R.drawable.c8s, 6);
		cards1.put(R.drawable.c9c, 305);
		cards1.put(R.drawable.c9d, 205);
		cards1.put(R.drawable.c9h, 105);
		cards1.put(R.drawable.c9s, 5);
		cards1.put(R.drawable.c10c, 304);
		cards1.put(R.drawable.c10d, 204);
		cards1.put(R.drawable.c10h, 104);
		cards1.put(R.drawable.c10s, 4);
		cards1.put(R.drawable.cjc, 303);
		cards1.put(R.drawable.cjd, 203);
		cards1.put(R.drawable.cjh, 103);
		cards1.put(R.drawable.cjs, 3);
		cards1.put(R.drawable.cqc, 302);
		cards1.put(R.drawable.cqd, 202);
		cards1.put(R.drawable.cqh, 102);
		cards1.put(R.drawable.cqs, 2);
		cards1.put(R.drawable.ckc, 301);
		cards1.put(R.drawable.ckd, 201);
		cards1.put(R.drawable.ckh, 101);
		cards1.put(R.drawable.cks, 1);
		cards1.put(R.drawable.cac, 300);
		cards1.put(R.drawable.cad, 200);
		cards1.put(R.drawable.cah, 100);
		cards1.put(R.drawable.cas, 0);
	}
}