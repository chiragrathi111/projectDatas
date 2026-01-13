
function initTraceability(data){
    Handlebars.registerHelper( "when",function(operand_1, operator, operand_2, options) {
        var operators = {
         'eq': function(l,r) { return l == r; },
         'noteq': function(l,r) { return l != r; },
         'gt': function(l,r) { return Number(l) > Number(r); },
         'or': function(l,r) { return l || r; },
         'and': function(l,r) { return l && r; },
         '%': function(l,r) { return (l % r) === 0; }
        }
        , result = operators[operator](operand_1,operand_2);
      
        if (result) return options.fn(this);
        else  return options.inverse(this);
      });

      
	console.log('Creating timeline...');

    // DOM element where the Timeline will be attached
    // var container = document.getElementById('visualization');
    var container = document.getElementsByClassName('traceability-visualization')[0];
    console.log('container: ', container);

    // var source   = document.getElementById('item-template').innerHTML;
    // var template = Handlebars.compile(document.getElementById('item-template').innerHTML);

    var template = Handlebars.compile(`<table class="score">
    
    {{#when what 'eq' 'received'}}
    <tr>
    <th>Received</th>
    </tr>
    {{/when}}

    {{#when what 'eq' 'stored'}}
    <tr>
    <th>Stored</th>
    </tr>
    {{/when}}

    {{#when what 'eq' 'qcaccepted'}}
    <tr>
    <th>QC Accepted</th>
    </tr>
    {{/when}}

    {{#when what 'eq' 'inernalmove'}}
    <tr>
    <th>Internal Move</th>
    </tr>
    {{/when}}
    
    {{#when what 'eq' 'picked'}}
    <tr>
    <th>Picked</th>
    </tr>
    {{/when}}
    
    {{#when what 'eq' 'dispatched'}}
    <tr>
    <th>Dispatched</th>
    </tr>
    {{/when}}
    
    <tr>
      <td>{{who}}</td>
    </tr>
    <tr>
        <td>{{where}}</td>
    </tr>

    {{#when what 'eq' 'stored'}}
    <th>To bin: {{toBin}}</th>
    {{/when}}

    {{#when what 'eq' 'inernalmove'}}
    <th>From bin: {{fromBin}}</th>
    <th>To bin: {{toBin}}</th>
    {{/when}}

    {{#when what 'eq' 'picked'}}
    <th>From bin: {{fromBin}}</th>
    {{/when}}

  </table>`);

var items = new vis.DataSet(data);
    // Create a DataSet (allows two way data-binding)
   /* var items = new vis.DataSet([
      // round of 16
      {
        who: 'Ram Gopal',
        what: 'received',
        where: 'Mumbai warehouse 1',
        start: '2023-11-29T13:00:00'
      }, {
        who: 'Gopal Kale',
        what: 'qcaccepted',
        where: 'Mumbai warehouse 1',
        start: '2023-12-01T13:00:00'
      }, {
        who: 'Sunil Kamble',
        what: 'stored',
        where: 'Mumbai warehouse 1',
        toBin: 'A-L-5-18',
        start: '2023-12-02T10:00:00'
      }, {
        who: 'Sunil Kamble',
        what: 'inernalmove',
        where: 'Mumbai warehouse 1',
        fromBin: 'A-L-5-18',
        toBin: 'B-R-5-2',
        start: '2023-12-03T11:30:00'
      }, {
        who: 'Sunil Kamble',
        what: 'inernalmove',
        where: 'Mumbai warehouse 1',
        fromBin: 'B-R-5-2',
        toBin: 'B-R-2-2',
        start: '2023-12-03T16:30:00'
      }, {
        who: 'Rahul Vasistha',
        what: 'picked',
        where: 'Mumbai warehouse 1',
        fromBin: 'B-R-2-2',
        start: '2023-12-05T16:30:00'
      }, {
        who: 'Rahul Vasistha',
        what: 'dispatched',
        where: 'Mumbai warehouse 1',
        start: '2023-12-06T16:30:00'
      }
    ]);*/
  
    // Configuration for the Timeline
    var options = {
      // specify a template for the items
      template: template
    };
  
    // Create a Timeline
    var timeline = new vis.Timeline(container, items, options);
}