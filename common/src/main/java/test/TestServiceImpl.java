package test;

/**
 * @author: sucf
 * @date: 2022/10/29 09:35
 * @description:
 */
public class TestServiceImpl implements TestService {
    @Override
    public String test(int i, String s) {
        return i + s;
    }
}
