class Table < Shoes::Widget
  
  #state = :enabled (default) or :disabled
  #sets the table responsive or non-responsive to clicks
  attr_writer :state
  
  #selected - returns the number of the curently selected row
  attr_reader :selected
  
  #block - sets a new Proc object to be called when row is clicked
  attr_writer :block
 
  #Sets up the table
  #top, left - position of the top and left corner of the table
  #height - number of rows to show without the vertical scrolling bar
  #headers - array of arrays containing headers and widths of the collumns)
  #          in the form of ["title", width]
  #items - array of arrays containing data to be displayed
  #blk - optional Proc object with a block to be called when the row is clicked

  def initialize opts = {}
    
    @block=opts[:blk]||nil
    @selected=nil
    @state=:enabled
    @height=opts[:rows]
    @items=opts[:items] 
    @headers=opts[:headers]
    @columns=@headers.size
    mult = @items.size > @height ? 1:0
    debug(mult)
    nostroke
    @width=2
    @item=[]
    @headers.each { |x| @width+=(x[1]+1)  }
    nostroke
    fill red
    @top=opts[:top]
    @left=opts[:left]
    @rec=rect :top => 0, :left => 0, :width=>@width+mult*12+2, :height=>31*(@height+1)+4 
    @lefty=0  
    
    @header=flow do       
        @headers.each_with_index do |h,l|
          temp=(l==@headers.size-1 ? h[1]+12*mult : h[1])
          debug("#{l} -> #{temp}")
          flow :top=>2,:left=>@lefty+2,:width=>temp,:height=>29 do
            rect(:top=>0,:left=>1,:width=>temp,:height=>29, :fill=>lightgrey)
            p=para strong(h[0]), :top=>2,  :align=>'center'
            @lefty+=h[1]+1
          end
        end 
      end

     @flot1=stack :width=>@width+mult*12+2, :height=>31*(@height), :scroll=>true, :top=>33, :left=>1 do
        @items.each_with_index do |it, i|
          inscription " "
          @item[i]=stack :width=>@width-1, :top=>31*i, :left=>1 do
            @lefty=0
            rr=[]
            @columns.times do |ei|
                rr[ei]=rect(:top=>1, :left=>@lefty+1, :width=>@headers[ei][1]-1,:height=>29, :fill=>white)
                it[ei]=" " if not it[ei] or it[ei]==""
                inscription strong(it[ei]), :top=>31*i+3, :left=>@lefty+2, :width=>@headers[ei][1]-1, :align=>'center'
                @lefty+=@headers[ei][1]+1

            end

            hover do
              if @state==:enabled
                @item[i].contents.each{|x| x.style(:fill=>dimgray)}
              end
            end
            leave do
              if @state==:enabled
                if @selected
                  if @selected==i
                    @item[i].contents.each{|x| x.style(:fill=>salmon)}
                  else
                    @item[i].contents.each{|x| x.style(:fill=>white)}
                  end
                else
                  @item[i].contents.each{|x| x.style(:fill=>white)}
                end
              end
            end
            click do
              if @state==:enabled
                if @selected
                  if @selected==i
                    @item[i].contents.each{|x| x.style(:fill=>white)}
                    @selected=nil
                  else
                    @item[@selected].contents.each{|x| x.style(:fill=>white)} 
                    @item[i].contents.each{|x| x.style(:fill=>salmon)}
                    @selected=i
                  end
                else
                  @item[i].contents.each{|x| x.style(:fill=>salmon)}
                  @selected=i
                end
                @block.call @items[i] if @selected and @block
              end  
            end         
          end
        end
      end
  end
    
  #-------------------------------
  #Allows for manual selection of the row
  def set_selected(item_no)
    if @selected
      @selected=item_no
      @item[@selected].contents.each{|x| x.contents[1].style(:fill=>salmon)}
    end
  end
 
  #Updates the current list of items shown in the table
  # items - array of items to show
  # height - height of table in rows
  def update_items(items, height=items.size)
    height=height if height<=items.size
    @rec.remove
    @header.remove
    @flot1.remove
    initialize(:top=>@top, :left=>@left,:rows=>height, :headers=>@headers, :items=>items, :blk=>@block)
  end 
  
end

=begin
Shoes.app do  
  @t=nil
  @z=Proc.new {|x| alert x}
  @y=Proc.new {|x| alert "Hej: #{x}"}
  stack do
    @t= table(:top=>50, :left=>10, :rows=>5, :headers=>[[:Title1, 80], [:Title2, 50], ["A Bit Loner Title", 200]],:items=>[[1,"","dssd"],["ererer",:mle,"ss"],[2,:ale,"sdsdsd"],[3,:ale,"ss"],[4,:mle,"ss"]], :blk=>@z)  
  
  end
  b1=button "hide" do
    @t.hide
  end
  b2=button "show" do
    @t.show
  end
  b3=button "disable" do
    @t.state=:disbled
  end
  b4=button "enable" do
    @t.state=:enabled
  end
  b5=button "update" do
    @t.update_items([[1,:esdfsdd,"dssd"],[4,:mal],[2,:fle,"sdsdsd"],[1,:mdfle,"dssd"],[4,:ale,"ss"],[2,:ale,"sdsdsd"]],4)
    @t.block=@y
  end
  b1.move(10,250)
  b2.move(70,250)
  b3.move(130,250)
  b4.move(220,250)
  b5.move(290,250)

end
=end
