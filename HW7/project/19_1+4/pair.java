
public class pair {
    public String value;
    public Integer count;
    
    public pair(String value, int count) {
        this.value = value;
        this.count = count;
    }
    
    @Override
    public String toString() {
        return (this.value + " -- " + this.count);
    }
}
