/*
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */

package com.tron.explorer.util;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.nio.*;
import java.nio.charset.Charset;

import com.tron.explorer.Constrain;

public interface Utils {
  SecureRandom random = new SecureRandom();

  static SecureRandom getRandom() {
    return random;
  }

  static byte[] getBytes(char[] chars) {
    Charset cs = Charset.forName("UTF-8");
    CharBuffer cb = CharBuffer.allocate(chars.length);
    cb.put(chars);
    cb.flip();
    ByteBuffer bb = cs.encode(cb);

    return bb.array();
  }

  public static String getIdShort(String Id) {
    return Id == null ? "<null>" : Id.substring(0, 8);
  }

  static char[] getChars(byte[] bytes) {
    Charset cs = Charset.forName("UTF-8");
    ByteBuffer bb = ByteBuffer.allocate(bytes.length);
    bb.put(bytes);
    bb.flip();
    CharBuffer cb = cs.decode(bb);

    return cb.array();
  }
  
  static byte[] clone(byte[] value) {
    byte[] clone = new byte[value.length];
    System.arraycopy(value, 0, clone, 0, value.length);
    return clone;
  }

  static String sizeToStr(long size) {
    if (size < 2 * (1L << 10)) return size + "b";
    if (size < 2 * (1L << 20)) return String.format("%dKb", size / (1L << 10));
    if (size < 2 * (1L << 30)) return String.format("%dMb", size / (1L << 20));
    return String.format("%dGb", size / (1L << 30));
  }
  
  static String getStrictAmount(long amount) {
	  DecimalFormat df = new DecimalFormat("###.######");
	  return df.format(Double.valueOf(amount)/Constrain.ONE_TRX);
  }
}
