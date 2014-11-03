package com.tencent.mapsdkdemo.raster;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tencent.mapsdkdemo.raster.MapOverlay.OnTapListener;
import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.ItemizedOverlay;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapController;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.OverlayItem;
import com.tencent.tencentmap.mapsdk.map.Projection;
import com.tencent.tencentmap.mapsdk.search.GeocoderSearch;
import com.tencent.tencentmap.mapsdk.search.PoiItem;
import com.tencent.tencentmap.mapsdk.search.ReGeocoderResult;
import com.tencent.tencentmap.mapsdk.search.ReGeocoderResult.ReGeocoderAddress;

/**
 * ItemizedOverlay实现地图上给定位置用自定义图片标三个点
 */
public class ItemizedOverlayDemo extends MapActivity {

	MapView mMapView;
	MapController mMapController;
	View viewTip = null;
	TextView addressTv = null;
	MapOverlay mapOverlay = null;

	int iTipTranslateX = 0;
	int iTipTranslateY = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.itemizedoverlay);

		LayoutInflater layoutInfla = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		viewTip = layoutInfla.inflate(R.layout.layouttipview, null);
		addressTv = (TextView) viewTip.findViewById(R.id.txtViewSelectSub);

		mMapView = (MapView) findViewById(R.id.itemizedoverlayview);
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		mMapController = mMapView.getController();

		Drawable marker = getResources().getDrawable(R.drawable.markpoint); // 得到需要标在地图上的资源

		this.iTipTranslateY = marker.getIntrinsicHeight();

		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight()); // 为maker定义位置和边界

		mapOverlay = new MapOverlay(marker, this);
		mapOverlay.setOnTapListener(onTapListener);
		mMapView.addOverlay(mapOverlay); // 添加标注，可以通过mMapView.getOverlays().remove删除标注，删除后可以通过mapview.refreshMap()刷新地图
											// 添加ItemizedOverlay实例到mMapView
		mMapView.invalidate(); // 刷新地图
		
		mMapController.animateTo(mapOverlay.getCenter());
		
		
	}

	OnTapListener onTapListener = new OnTapListener() {

		@Override
		public void onTap(OverlayItem itemTap, String address) {
			// TODO Auto-generated method stub
			if (viewTip == null || itemTap == null) {
				return;
			}
			addressTv.setText(address);
			MapView.LayoutParams layParaOntap = new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, itemTap.getPoint(),
					iTipTranslateX, -iTipTranslateY,
					MapView.LayoutParams.BOTTOM_CENTER);
			if (mMapView.indexOfChild(viewTip) == -1) {
				mMapView.addView(viewTip, layParaOntap);
			} else {
				mMapView.updateViewLayout(viewTip, layParaOntap);
			}
		}

		@Override
		public void onEmptyTap(GeoPoint pt) {
			// TODO Auto-generated method stub
			if (mMapView.indexOfChild(viewTip) >= 0) {
				mMapView.removeView(viewTip);
			}
		}
	};

}

class MapOverlay extends ItemizedOverlay<OverlayItem> {

	GeocoderSearch geocodersearcher = null;

	private List<OverlayItem> itemList = new ArrayList<OverlayItem>();
	// private Context mContext;
	private OnTapListener tapListener = null;

	private boolean isLongClickMarker = false;

	private double mLat1 = 23.138727; // point1纬度
	private double mLon1 = 113.335559; // point1经度


	String strResult = "";

	public MapOverlay(Drawable marker, Context context) {
		super(boundCenterBottom(marker));

		geocodersearcher = new GeocoderSearch(context);

		// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
		GeoPoint p1 = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 * 1E6));

		// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
		itemList.add(new OverlayItem(p1, "1", "已选中第一个点"));
		populate(); // createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
	}

	@Override
	public void draw(Canvas canvas, MapView mapView) {

		// Projection接口用于屏幕像素点坐标系统和地球表面经纬度点坐标系统之间的变换
		Projection projection = mapView.getProjection();
		for (int index = size() - 1; index >= 0; index--) { // 遍历GeoList
			OverlayItem overLayItem = getItem(index); // 得到给定索引的item

			String title = overLayItem.getTitle();
			// 把经纬度变换到相对于MapView左上角的屏幕像素坐标
			Point point = projection.toPixels(overLayItem.getPoint(), null);

			Paint paintCircle = new Paint();
			paintCircle.setColor(Color.RED);
			canvas.drawCircle(point.x, point.y, 5, paintCircle); // 画圆

			Paint paintText = new Paint();
			paintText.setColor(Color.BLACK);
			paintText.setTextSize(15);
			canvas.drawText(title, point.x, point.y - 25, paintText); // 绘制文本

		}

		super.draw(canvas, mapView);
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return itemList.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return itemList.size();
	}

	@Override
	protected boolean onTap(int i) {
		OverlayItem itemSelect = itemList.get(i);
		setFocus(itemSelect);
		if (tapListener != null) {
			tapListener.onTap(itemSelect, strResult);
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0, MapView arg1) {

		if (isLongClickMarker && (arg0.getAction() == MotionEvent.ACTION_UP)) {
			
			isLongClickMarker = false;
			
			// 得到拖动停止的位置
			OverlayItem oi = getItem(0);

			ReGeocoderResult regeocoderResult = null;
			try {

				arg1.getController().animateTo(oi.getPoint());
				regeocoderResult = geocodersearcher.searchFromLocation(oi
						.getPoint());

				// String nextLine = System.getProperty("line.separator");
				//
				// if(regeocoderResult.addresslist!=null)
				// {
				// int iAddrSize=regeocoderResult.addresslist.size();
				// strResult+="addresslist size="+iAddrSize+nextLine;
				// if(iAddrSize>0)
				// {
				// ReGeocoderAddress addr=regeocoderResult.addresslist.get(0);
				// strResult+="address name="+addr.name+nextLine;
				// strResult+="address type="+addr.type+nextLine;
				// strResult+="address dist="+addr.dist+nextLine;
				// strResult+="address adcode="+addr.adcode+nextLine;
				// strResult+="address point="+addr.point.toString()+nextLine;
				// }
				// }
				// if(regeocoderResult.poilist!=null)
				// {
				// int iPoiSize=regeocoderResult.poilist.size();
				// strResult+="poilist size="+iPoiSize+nextLine;
				// if(iPoiSize>0)
				// {
				// PoiItem poi=regeocoderResult.poilist.get(0);
				// strResult+="poi name="+poi.name+nextLine;
				// strResult+="poi address="+poi.address+nextLine;
				// strResult+="poi classes="+poi.classes+nextLine;
				// strResult+="poi phone="+poi.phone+nextLine;
				// strResult+="poi point="+poi.point.toString()+nextLine;
				// }
				// }

				if (regeocoderResult.poilist != null) {
					int iPoiSize = regeocoderResult.poilist.size();
					if (iPoiSize > 0) {
						PoiItem poi = regeocoderResult.poilist.get(0);
						strResult = poi.address + poi.name;
					}
				}

				System.out.println(strResult);
				onTap(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.onTouchEvent(arg0, arg1);
	}

	@Override
	public boolean onLongPress(GeoPoint p, MotionEvent event, MapView mapView) {
		// TODO Auto-generated method stub
		isLongClickMarker = true;
		return super.onLongPress(p, event, mapView);
	}

	@Override
	public void onEmptyTap(GeoPoint pt) {
		// TODO Auto-generated method stub
		super.onEmptyTap(pt);

		if (tapListener != null) {
			tapListener.onEmptyTap(pt);
		}
	}

	interface OnTapListener {
		void onTap(OverlayItem itemTap, String address);

		void onEmptyTap(GeoPoint pt);
	}

	public void setOnTapListener(OnTapListener listnerTap) {
		tapListener = listnerTap;
	}
}
