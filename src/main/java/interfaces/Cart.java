package main.java.interfaces;

public interface Cart
{
   public void add(String code);
   public void add(String code, String promo);
   public void total();
   public void items();
}