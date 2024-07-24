package xyz.cofe.nixpath;

import xyz.cofe.coll.im.Result;

import java.util.ArrayList;
import java.util.List;

public final class CanonAbsPath implements NixPath {
    private final List<Name.Regular> pathComponents;
    private final boolean endsWithSlash;

    public static final CanonAbsPath root;
    static {
        root = new CanonAbsPath(List.of(),false);
    }

    public CanonAbsPath(Iterable<Name.Regular> pathComponents, boolean endsWithSlash ){
        if( pathComponents==null ) throw new IllegalArgumentException("pathComponents==null");
        this.endsWithSlash = endsWithSlash;
        this.pathComponents = new ArrayList<>();

        var lst = new ArrayList<Name>();
        pathComponents.forEach(lst::add);
        pathComponents1 = lst;
    }

    private CanonAbsPath(List<Name.Regular> pathComponents, boolean endsWithSlash ){
        this.endsWithSlash = endsWithSlash;
        this.pathComponents = pathComponents;

        pathComponents1 = new ArrayList<Name>(pathComponents);
    }

    private final List<Name> pathComponents1;

    @Override
    public List<Name> getPathComponents() {
        return pathComponents1;
    }

    @Override
    public boolean isStartWithSlash() {
        return true;
    }

    @Override
    public boolean isEndsWithSlash() {
        return endsWithSlash;
    }

    public static Result<CanonAbsPath,String> parse(NixPath path){
        if( path==null ) throw new IllegalArgumentException("path==null");
        if( path.isEmpty() )return Result.error("path is empty");
        if( !path.isAbsolute() )return Result.error("path not absolute");

        var names = new ArrayList<Name.Regular>();
        for( Name name : path.getPathComponents() ){
            if( name instanceof Name.Regular r ){
                names.add(r);
            }else if( name instanceof Name.ParentDir p ){
                return Result.error("contains Name.ParentDir (..)");
            }else if( name instanceof Name.ThisDir p ){
                return Result.error("contains Name.ThisDir (..)");
            }
        }

        return Result.ok( new CanonAbsPath(names, path.isEndsWithSlash()) );
    }

    public static Result<CanonAbsPath,String> parse(String str){
        if( str==null ) throw new IllegalArgumentException("str==null");
        return UnixPath.parse(str).fmap(CanonAbsPath::parse);
    }

    public Result<CanonAbsPath,String> parent(){
        if( pathComponents.isEmpty() )return Result.error("root not contains parent");
        if( pathComponents.size()==1 )return Result.ok(root);

        List<Name.Regular> names = new ArrayList<>(pathComponents);
        names.remove(names.size()-1);

        return Result.ok(new CanonAbsPath(names,true));
    }

    public Result<CanonAbsPath,String> resolve(NixPath path){
        if( path==null ) throw new IllegalArgumentException("path==null");
        if( path.isEmpty() )return Result.ok(this);

        if( path.isAbsolute() && path.isEscapeOfRoot() ){
            return Result.error("path("+path+") is escape of root");
        }

        var res = path.isAbsolute() ? root : this;
        var lst = new ArrayList<Name>(path.getPathComponents());

        while (!lst.isEmpty()){
            var name = lst.remove(0);
            if( name instanceof Name.Regular rname ){
                res = res.addName(rname, false);
            }else if( name instanceof Name.ParentDir pname ){
                var prnt = res.parent();
                if(prnt.isError()) return prnt;
                res = prnt.fold( a -> a, b -> {throw new RuntimeException("!");});
            }
        }

        return Result.ok(new CanonAbsPath(res.pathComponents, path.isEndsWithSlash()));
    }

    public Result<CanonAbsPath,String> resolve(String path){
        if( path==null ) throw new IllegalArgumentException("path==null");
        return UnixPath.parse(path).fmap(this::resolve);
    }

    @SuppressWarnings("SameParameterValue")
    private CanonAbsPath addName(Name.Regular rname, boolean endsWithSlash){
        var lst = new ArrayList<>(pathComponents);
        lst.add(rname);
        return new CanonAbsPath(lst, endsWithSlash);
    }

    public String toString(){
        return NixPath.toString(this);
    }

}
