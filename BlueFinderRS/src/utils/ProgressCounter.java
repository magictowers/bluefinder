//
// Copyright (c) 2012 Mirko Nasato
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the "Software"),
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
// THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
// OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
// ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// OTHER DEALINGS IN THE SOFTWARE.
//
package utils;

public class ProgressCounter {

	private int thousand = 1000;
	private int smallStep = 1 * thousand;
	private int bigStep = 50 * thousand;
    private String str = "k";

	private int count = 0;

	public int getCount() {
		return count;
	}
    
    public ProgressCounter() {}
    
    public ProgressCounter(int count, String str) {
        thousand = count;
        smallStep = 1 * count;
        bigStep = 50 * count;
        this.str = str;
    }

	public void increment() {
		count++;
		if (count % bigStep == 0) {
			System.out.println(". " + count / thousand + this.str);
		} else if (count % smallStep == 0) {
			System.out.print(".");
		}
	}
}
