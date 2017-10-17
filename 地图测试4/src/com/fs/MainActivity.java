package com.fs;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationSource,
AMapLocationListener{
	MapView mapView;
	AMap aMap;
	AMapLocationClient mlocationClient;
	AMapLocationClientOption mLocationOption;
	OnLocationChangedListener mListener;
	UiSettings mUiSettings;
	CameraUpdate mUpdata;
	Marker marker;
	String search_longitude,search_latitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String defalut_longitude = "30";
		String defalut_latitude = "107";
		
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.onCreate(savedInstanceState);
		init(defalut_longitude,defalut_latitude);
		
		Log.d("xjh","创建");
	}
	private void init(String longitude,String latitude) {
			aMap = mapView.getMap();
			mUiSettings = aMap.getUiSettings();
			mUiSettings.setZoomControlsEnabled(false);
			mUiSettings.setCompassEnabled(true);
			mUpdata = CameraUpdateFactory.newCameraPosition(new CameraPosition(
					new LatLng( Double.parseDouble(longitude), Double.parseDouble(latitude)), 15, 0, 30));
			aMap.moveCamera(mUpdata);
			drawMarkers(longitude,latitude);
	}

	private void drawMarkers(String longitude,String latitude) {
		marker = aMap.addMarker(new MarkerOptions()
				.position(new LatLng( Double.parseDouble(longitude), Double.parseDouble(latitude)))
				.title("查找的地点")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.draggable(true));
		marker.showInfoWindow();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("xjh","重新处于活动状态");
		mapView.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("xjh","暂停");
		mapView.onPause();
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		if (null != mlocationClient) {
			mlocationClient.onDestroy();
		}
		Log.d("xjh","被销毁了");
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mlocationClient == null) {
			mlocationClient = new AMapLocationClient(this);
			mLocationOption = new AMapLocationClientOption();
			mlocationClient.setLocationListener(this);
			mlocationClient.setLocationOption(mLocationOption);
			mlocationClient.startLocation();
		}
	}

	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null && amapLocation.getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);
			} else {
				String errText = "定位失败" + amapLocation.getErrorCode() + ";"
						+ amapLocation.getErrorInfo();
				Log.e("map", errText);
			}
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		menu.add(0,1,1,"查找");
		menu.add(0,2,2,"其他1");
		menu.add(0,3,3,"其他2");
		menu.add(0,4,4,"其他3");
		menu.add(0,5,5,"其他4");
		return super.onCreateOptionsMenu(menu);	
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_search:
			search();
			return true;
		case 1:
			search();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	private void search() {
		AlertDialog dialog = null;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("查找");
		
		View view = getLayoutInflater().inflate(R.layout.search, null);
		final EditText edt_longitude = (EditText) view.findViewById(R.id.edt_longitude);
		final EditText edt_latitude = (EditText) view.findViewById(R.id.edt_latitude);
		
		builder.setView(view);
		
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				search_longitude = edt_longitude.getText().toString();
				search_latitude = edt_latitude.getText().toString();
				Toast.makeText(MainActivity.this,search_longitude, Toast.LENGTH_LONG).show();
				marker.remove();
				init(search_longitude, search_latitude);
			}
		});
		dialog = builder.create();
		dialog.show();
	}
}
