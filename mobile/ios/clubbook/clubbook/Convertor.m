//
//  Convertor.m
//  Clubbook
//
//  Created by Andrew on 6/21/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "Convertor.h"

@implementation Convertor
+ (NSString *)convertDictionaryToJsonString:(NSDictionary *)successResult {
    // convert data to json string
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:successResult
                                                       options:0
                                                         error:nil];
    
    NSString *JSONresult = [[NSString alloc] initWithBytes:[jsonData bytes] length:[jsonData length] encoding:NSUTF8StringEncoding];
    return JSONresult;
}
@end
