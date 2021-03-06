package com.fnz.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.fnz.VO.CustomerVO;
import com.fnz.VO.FinshedProductVO;
import com.fnz.VO.OrderVO;
import com.fnz.VO.VendorVO;
import com.fnz.common.CommonConstants;
import com.fnz.common.SQLConstants;

public class OrderDAO 
{
	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");  
	/*Create Method <function name><return type><comments>
	 * <Creator Name><Date Of Creation MM-dd-yyyy>
	 * 
	 * <addOrder(CustomerVO, OrderVO)><void><add an client order to DB>
	 * <Abhinay Agarwal><12-10-2012>
	 * 
	 * */
	/**Modification Log
	 * 
	 * <Date> <Name> <Comments>
	 * 
	 */
	public void addOrder(CustomerVO customerVO, OrderVO orderVO, ObservableList<String> clientList) throws Exception 
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		ResultSet resultSet = null;
		Integer latestRow = 0;
		String newOrderNumber = CommonConstants.ORDER_NUMBER;
		String newCustNumber = CommonConstants.CUST_NUMBER;
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			conn = DriverManager.getConnection(sDbUrl);
			pstmt = conn.prepareStatement(SQLConstants.INSERT_CUSTOMER);
			pstmt1 = conn.prepareStatement(SQLConstants.INSERT_CLIENT_ORDER);
			pstmt2 = conn.prepareStatement(SQLConstants.FETCH_LATEST_ORDER);
			pstmt3 = conn.prepareStatement(SQLConstants.FETCH_LATEST_CUST);
			
			resultSet = pstmt2.executeQuery();
			
			resultSet.next();
			
			latestRow = resultSet.getInt(1)+1;
			
			if(latestRow <10)
			{
				newOrderNumber = newOrderNumber + "00" + latestRow.toString();
			}
			else if(latestRow >=10 && latestRow <100)
			{
				newOrderNumber = newOrderNumber + "0" + latestRow.toString();
			}
			else
			{
				newOrderNumber = newOrderNumber + latestRow.toString();
			}
			
			resultSet = pstmt3.executeQuery();
			resultSet.next();
			
			latestRow = resultSet.getInt(1)+1;
			
			if(latestRow <10)
			{
				newCustNumber = newCustNumber + "00" + latestRow.toString();
			}
			else if(latestRow >=10 && latestRow <100)
			{
				newCustNumber = newCustNumber + "0" + latestRow.toString();
			}
			else
			{
				newCustNumber = newCustNumber + latestRow.toString();
			}
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			
			
			if(!clientList.contains(customerVO.getCustomerName()))
			{
				pstmt.setString(1, newCustNumber);
				pstmt.setString(2, Character.toString(customerVO.getCustomerName().toLowerCase().charAt(0)).toUpperCase()+customerVO.getCustomerName().toLowerCase().substring(1));
				pstmt.setString(3, customerVO.getCustomerAddress());
				pstmt.setString(4, customerVO.getCustomerPhone());
				pstmt.setString(5, customerVO.getCustomerEmail());
				pstmt.execute();
			}
			
			pstmt1.setString(1, newOrderNumber);
			pstmt1.setString(2, format.format(new Date()).toString());
			pstmt1.setString(3, Character.toString(orderVO.getCustomerName().toLowerCase().charAt(0)).toUpperCase()+orderVO.getCustomerName().toLowerCase().substring(1));
			pstmt1.setInt(4, orderVO.getOrderQuantity());
			pstmt1.setInt(5, orderVO.getOrderDelivered());
			pstmt1.setDouble(6, orderVO.getAmount());
			pstmt1.setDouble(7, orderVO.getAdvance());
			pstmt1.setString(8, orderVO.getDod());
			pstmt1.setString(9, orderVO.getStatus());
			
			pstmt1.execute();
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(pstmt1 != null )
			{
				pstmt1.close();
			}
			if(pstmt2 != null )
			{
				pstmt2.close();
			}
			if(pstmt3 != null )
			{
				pstmt3.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
	}

	/*Create Method <function name><return type><comments>
	 * <Creator Name><Date Of Creation MM-dd-yyyy>
	 * 
	 * <viewOrder()><ObservableList<OrderVO>><view an client order from DB>
	 * <Abhinay Agarwal><12-10-2012>
	 * 
	 * */
	/**Modification Log
	 * 
	 * <Date> <Name> <Comments>
	 * 
	 */
	public ObservableList<OrderVO> viewOrder() throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		ObservableList<OrderVO> list = FXCollections.observableArrayList();
		OrderVO orderVO = new OrderVO();
		
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			conn = DriverManager.getConnection(sDbUrl);
			pstmt = conn.prepareStatement(SQLConstants.FETCH_CLIENT_ORDER);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			resultSet = pstmt.executeQuery();
			
			while(resultSet.next())
			{
				orderVO = new OrderVO();
				orderVO.setOrderNo(resultSet.getString(1));
				orderVO.setDate(resultSet.getString(2));
				orderVO.setCustomerName(resultSet.getString(3));
				orderVO.setOrderQuantity(resultSet.getInt(4));
				orderVO.setOrderDelivered(resultSet.getInt(5));
				orderVO.setOrderPending(orderVO.getOrderQuantity() - orderVO.getOrderDelivered());
				orderVO.setAmount(resultSet.getDouble(6));
				orderVO.setAdvance(resultSet.getDouble(7));
				orderVO.setDod(resultSet.getString(8));
				orderVO.setStatus(resultSet.getString(9));
				list.add(orderVO);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
		return list;
	}
	
	/*Create Method <function name><return type><comments>
	 * <Creator Name><Date Of Creation MM-dd-yyyy>
	 * 
	 * <addOrder(CustomerVO, OrderVO)><void><add an client order to DB>
	 * <Abhinay Agarwal><12-10-2012>
	 * 
	 * */
	/**Modification Log
	 * 
	 * <Date> <Name> <Comments>
	 * 
	 */
	public void addVendorOrder(VendorVO vendorVO, OrderVO orderVO, ObservableList<String> vendorList) throws Exception 
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		ResultSet resultSet = null;
		Integer latestRow = 0;
		String newOrderNumber = CommonConstants.PURCHASE_NUMBER;
		String newCustNumber = CommonConstants.CUST_NUMBER;
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			conn = DriverManager.getConnection(sDbUrl);
			pstmt = conn.prepareStatement(SQLConstants.INSERT_VENDOR);
			pstmt1 = conn.prepareStatement(SQLConstants.INSERT_VENDOR_ORDER);
			pstmt2 = conn.prepareStatement(SQLConstants.FETCH_LATEST_VENDOR_ORDER);
			pstmt3 = conn.prepareStatement(SQLConstants.FETCH_LATEST_VENDOR);
			
			resultSet = pstmt2.executeQuery();
			
			resultSet.next();
			
			latestRow = resultSet.getInt(1)+1;
			
			if(latestRow <10)
			{
				newOrderNumber = newOrderNumber + "00" + latestRow.toString();
			}
			else if(latestRow >=10 && latestRow <100)
			{
				newOrderNumber = newOrderNumber + "0" + latestRow.toString();
			}
			else
			{
				newOrderNumber = newOrderNumber + latestRow.toString();
			}
			
			resultSet = pstmt3.executeQuery();
			resultSet.next();
			
			latestRow = resultSet.getInt(1)+1;
			
			if(latestRow <10)
			{
				newCustNumber = newCustNumber + "00" + latestRow.toString();
			}
			else if(latestRow >=10 && latestRow <100)
			{
				newCustNumber = newCustNumber + "0" + latestRow.toString();
			}
			else
			{
				newCustNumber = newCustNumber + latestRow.toString();
			}
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			
			if(!vendorList.contains(vendorVO.getVendorName()))
			{
				pstmt.setString(1, newCustNumber );
				pstmt.setString(2, Character.toString(vendorVO.getVendorName().toLowerCase().charAt(0)).toUpperCase()+vendorVO.getVendorName().toLowerCase().substring(1));
				pstmt.setString(3, vendorVO.getVendorAddress());
				pstmt.setString(4, vendorVO.getVendorPhone());
				pstmt.setString(5, vendorVO.getVendorEmail());
				pstmt.execute();
			}
			
			pstmt1.setString(1, newOrderNumber);
			pstmt1.setString(2, format.format(new Date()).toString());
			pstmt1.setString(3, Character.toString(orderVO.getCustomerName().toLowerCase().charAt(0)).toUpperCase()+orderVO.getCustomerName().toLowerCase().substring(1));
			pstmt1.setInt(4, orderVO.getOrderQuantity());
			pstmt1.setInt(5, orderVO.getOrderDelivered());
			pstmt1.setDouble(6, orderVO.getAmount());
			pstmt1.setDouble(7, orderVO.getAdvance());
			pstmt1.setString(8, orderVO.getDod());
			pstmt1.setString(9, orderVO.getStatus());
			
			pstmt1.execute();
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(pstmt1 != null )
			{
				pstmt1.close();
			}
			if(pstmt2 != null )
			{
				pstmt2.close();
			}
			if(pstmt3 != null )
			{
				pstmt3.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
	}
	/*Create Method <function name><return type><comments>
	 * <Creator Name><Date Of Creation MM-dd-yyyy>
	 * 
	 * <viewOrder()><ObservableList<OrderVO>><view an client order from DB>
	 * <Abhinay Agarwal><12-10-2012>
	 * 
	 * */
	/**Modification Log
	 * 
	 * <Date> <Name> <Comments>
	 * 
	 */
	public ObservableList<OrderVO> viewVendorOrder() throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		ObservableList<OrderVO> list = FXCollections.observableArrayList();
		OrderVO orderVO = new OrderVO();
		
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			conn = DriverManager.getConnection(sDbUrl);
			pstmt = conn.prepareStatement(SQLConstants.FETCH_VENDOR_ORDER);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			resultSet = pstmt.executeQuery();
			
			while(resultSet.next())
			{
				orderVO = new OrderVO();
				orderVO.setOrderNo(resultSet.getString(1));
				orderVO.setDate(resultSet.getString(2));
				orderVO.setCustomerName(resultSet.getString(3));
				orderVO.setOrderQuantity(resultSet.getInt(4));
				orderVO.setOrderDelivered(resultSet.getInt(5));
				orderVO.setOrderPending(orderVO.getOrderQuantity()-orderVO.getOrderDelivered());
				orderVO.setAmount(resultSet.getDouble(6));
				orderVO.setAdvance(resultSet.getDouble(7));
				orderVO.setDod(resultSet.getString(8));
				orderVO.setStatus(resultSet.getString(9));
				list.add(orderVO);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
		return list;
	}
	/*Create Method <function name><return type><comments>
	 * <Creator Name><Date Of Creation MM-dd-yyyy>
	 * 
	 * <viewOrder()><ObservableList<OrderVO>><view an client order from DB>
	 * <Abhinay Agarwal><12-10-2012>
	 * 
	 * */
	/**Modification Log
	 * 
	 * <Date> <Name> <Comments>
	 * 
	 */
	public ObservableList<String> viewVendors() throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		ObservableList<String> list = FXCollections.observableArrayList();
		
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			conn = DriverManager.getConnection(sDbUrl);
			pstmt = conn.prepareStatement(SQLConstants.FETCH_VENDORS);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			resultSet = pstmt.executeQuery();
			
			while(resultSet.next())
			{
				list.add(resultSet.getString(1));
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
		return list;
	}
	
	
	/*Create Method <function name><return type><comments>
	 * <Creator Name><Date Of Creation MM-dd-yyyy>
	 * 
	 * <viewOrder()><ObservableList<OrderVO>><view an client order from DB>
	 * <Abhinay Agarwal><12-10-2012>
	 * 
	 * */
	/**Modification Log
	 * 
	 * <Date> <Name> <Comments>
	 * 
	 */
	public ObservableList<String> viewPurchaseList(String vendorName) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		ObservableList<String> list = FXCollections.observableArrayList();
		
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			conn = DriverManager.getConnection(sDbUrl);
			pstmt = conn.prepareStatement(SQLConstants.FETCH_VENDOR_PURCHASES);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			pstmt.setString(1,vendorName);
			resultSet = pstmt.executeQuery();
			
			while(resultSet.next())
			{
				list.add(resultSet.getString(1));
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
		return list;
	}

	/*Create Method <function name><return type><comments>
	 * <Creator Name><Date Of Creation MM-dd-yyyy>
	 * 
	 * <viewOrder()><ObservableList<OrderVO>><view an client order from DB>
	 * <Abhinay Agarwal><12-10-2012>
	 * 
	 * */
	/**Modification Log
	 * 
	 * <Date> <Name> <Comments>
	 * 
	 */
	public ObservableList<String> viewClients() throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		ObservableList<String> list = FXCollections.observableArrayList();
		
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			conn = DriverManager.getConnection(sDbUrl);
			pstmt = conn.prepareStatement(SQLConstants.FETCH_CLIENTS);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			resultSet = pstmt.executeQuery();
			
			while(resultSet.next())
			{
				list.add(resultSet.getString(1));
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
		return list;
	}
	
	
	/*Create Method <function name><return type><comments>
	 * <Creator Name><Date Of Creation MM-dd-yyyy>
	 * 
	 * <viewOrder()><ObservableList<OrderVO>><view an client order from DB>
	 * <Abhinay Agarwal><12-10-2012>
	 * 
	 * */
	/**Modification Log
	 * 
	 * <Date> <Name> <Comments>
	 * 
	 */
	public ObservableList<String> viewOrderList(String vendorName) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		ObservableList<String> list = FXCollections.observableArrayList();
		
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			conn = DriverManager.getConnection(sDbUrl);
			pstmt = conn.prepareStatement(SQLConstants.FETCH_CLIENT_ORDERS);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			pstmt.setString(1,vendorName);
			resultSet = pstmt.executeQuery();
			
			while(resultSet.next())
			{
				list.add(resultSet.getString(1));
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
		return list;
	}
	
	/*Create Method <function name><return type><comments>
	 * <Creator Name><Date Of Creation MM-dd-yyyy>
	 * 
	 * <viewOrder()><ObservableList<OrderVO>><view an client order from DB>
	 * <Abhinay Agarwal><12-10-2012>
	 * 
	 * */
	/**Modification Log
	 * 
	 * <Date> <Name> <Comments>
	 * 
	 */
	public void cancelClientOrder(String orderNumber) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			conn = DriverManager.getConnection(sDbUrl);
			pstmt = conn.prepareStatement(SQLConstants.CANCEL_CLIENT_ORDERS);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			pstmt.setString(1,orderNumber);
			pstmt.executeUpdate();
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
	}
	/*Create Method <function name><return type><comments>
	 * <Creator Name><Date Of Creation MM-dd-yyyy>
	 * 
	 * <viewOrder()><ObservableList<OrderVO>><view an client order from DB>
	 * <Abhinay Agarwal><12-10-2012>
	 * 
	 * */
	/**Modification Log
	 * 
	 * <Date> <Name> <Comments>
	 * 
	 */
	public void cancelVendorOrder(String orderNumber) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			conn = DriverManager.getConnection(sDbUrl);
			pstmt = conn.prepareStatement(SQLConstants.CANCEL_VENDOR_ORDERS);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			pstmt.setString(1,orderNumber);
			pstmt.executeUpdate();
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
	}
	

	public OrderVO fetchClientOrderDetails(String orderNo) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		OrderVO orderVO = new OrderVO();
		
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			conn = DriverManager.getConnection(sDbUrl);
			pstmt = conn.prepareStatement(SQLConstants.FETCH_CLIENT_ORDER_DETAILS);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			pstmt.setString(1, orderNo);
			resultSet = pstmt.executeQuery();
			
			while(resultSet.next())
			{
				orderVO = new OrderVO();
				orderVO.setOrderNo(resultSet.getString(1));
				orderVO.setDate(resultSet.getString(2));
				orderVO.setCustomerName(resultSet.getString(3));
				orderVO.setOrderQuantity(resultSet.getInt(4));
				orderVO.setOrderDelivered(resultSet.getInt(5));
				orderVO.setOrderPending(orderVO.getOrderQuantity() - orderVO.getOrderDelivered());
				orderVO.setAmount(resultSet.getDouble(6));
				orderVO.setAdvance(resultSet.getDouble(7));
				orderVO.setDod(resultSet.getString(8));
				orderVO.setStatus(resultSet.getString(9));
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
		return orderVO;
	}
	

	public OrderVO fetchVendorOrderDetails(String orderNo) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		OrderVO orderVO = new OrderVO();
		
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			conn = DriverManager.getConnection(sDbUrl);
			pstmt = conn.prepareStatement(SQLConstants.FETCH_VENDOR_ORDER_DETAILS);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			pstmt.setString(1, orderNo);
			resultSet = pstmt.executeQuery();
			
			while(resultSet.next())
			{
				orderVO = new OrderVO();
				orderVO.setOrderNo(resultSet.getString(1));
				orderVO.setDate(resultSet.getString(2));
				orderVO.setCustomerName(resultSet.getString(3));
				orderVO.setOrderQuantity(resultSet.getInt(4));
				orderVO.setOrderDelivered(resultSet.getInt(5));
				orderVO.setOrderPending(orderVO.getOrderQuantity() - orderVO.getOrderDelivered());
				orderVO.setAmount(resultSet.getDouble(6));
				orderVO.setAdvance(resultSet.getDouble(7));
				orderVO.setDod(resultSet.getString(8));
				orderVO.setStatus(resultSet.getString(9));
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
		return orderVO;
	}
	
	public void editClientOrder(String orderNo, int quantityDelivered, double amountReceived ) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		OrderVO orderVO = new OrderVO();
		String status=null;
		
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{   
		    orderVO = fetchClientOrderDetails(orderNo);
			
		    conn = DriverManager.getConnection(sDbUrl);
			
		    pstmt = conn.prepareStatement(SQLConstants.UPDATE_CLIENT_ORDER_DETAILS);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			
			if(orderVO.getOrderDelivered()+quantityDelivered==0)
			{
				status=CommonConstants.STATUS_PENDING;
			}
			else if(orderVO.getOrderDelivered()+quantityDelivered>0 && orderVO.getOrderDelivered()+quantityDelivered<orderVO.getOrderQuantity())
			{
				status=CommonConstants.STATUS_PARTIALLY_DELIVERED;
			}
			else
			{
				status=CommonConstants.STATUS_DELIVERED;
			}
			pstmt.setInt(1, orderVO.getOrderDelivered()+quantityDelivered);
			pstmt.setDouble(2, orderVO.getAdvance()+amountReceived);
			pstmt.setString(3, status);
			pstmt.setString(4, orderNo);
			pstmt.executeUpdate();
			
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
	}
	
	public void editVendorOrder(String orderNo, int quantityDelivered, double amountReceived ) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		OrderVO orderVO = new OrderVO();
		
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
		    
		    orderVO = fetchClientOrderDetails(orderNo);
			
		    conn = DriverManager.getConnection(sDbUrl);
			
		    pstmt = conn.prepareStatement(SQLConstants.UPDATE_VENDOR_PURCHASE_DETAILS);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			pstmt.setInt(1, orderVO.getOrderDelivered()+quantityDelivered);
			pstmt.setDouble(2, orderVO.getAdvance()+amountReceived);
			pstmt.setString(3, orderNo);
			pstmt.executeUpdate();
			
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
	}
	
	public ObservableList<CustomerVO> fetchClientDetails() throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		CustomerVO customerVO = new CustomerVO();
		ObservableList<CustomerVO> list=FXCollections.observableArrayList();
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			
		    conn = DriverManager.getConnection(sDbUrl);
			
		    pstmt = conn.prepareStatement(SQLConstants.FETCH_CLIENT_DETAILS);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			resultSet = pstmt.executeQuery();
			
			while(resultSet.next())
			{
				customerVO = new CustomerVO();
				customerVO.setCustomerName(resultSet.getString(2));
				customerVO.setCustomerAddress(resultSet.getString(3));
				customerVO.setCustomerPhone(resultSet.getString(4));
				customerVO.setCustomerEmail(resultSet.getString(5));
				list.add(customerVO);
			}
			
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
		return list;
	}
	public ObservableList<VendorVO> fetchVendorDetails() throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		VendorVO vendorVO = new VendorVO();
		ObservableList<VendorVO> list=FXCollections.observableArrayList();
		
		Class.forName(CommonConstants.DRIVERNAME);
		
		String sDbUrl = CommonConstants.sJdbc + ":" + CommonConstants.DB_LOCATION + CommonConstants.sTempDb;
		
		try 
		{
			
		    conn = DriverManager.getConnection(sDbUrl);
			
		    pstmt = conn.prepareStatement(SQLConstants.FETCH_VENDOR_DETAILS);
			
			pstmt.setQueryTimeout(CommonConstants.TIMEOUT);
			resultSet = pstmt.executeQuery();
			
			while(resultSet.next())
			{
				vendorVO = new VendorVO();
				vendorVO.setVendorName(resultSet.getString(2));
				vendorVO.setVendorAddress(resultSet.getString(3));
				vendorVO.setVendorPhone(resultSet.getString(4));
				vendorVO.setVendorEmail(resultSet.getString(5));
				list.add(vendorVO);
				
			}
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(conn !=null )
			{
				conn.close();
			}
			if(pstmt != null )
			{
				pstmt.close();
			}
			if(resultSet != null)
			{
				resultSet.close();
			}
		}
		return list;
	}
}


