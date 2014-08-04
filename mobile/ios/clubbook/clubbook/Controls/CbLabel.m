//
//  CbLabel.m
//  Clubbook
//
//  Created by Andrew on 7/6/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "CbLabel.h"

@implementation CbLabel

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

-(float)resizeToFit{
    float height = [self expectedHeight];
    CGRect newFrame = [self frame];
    newFrame.size.height = height;
    [self setFrame:newFrame];
    return newFrame.origin.y + newFrame.size.height;
}

-(float)expectedHeight{
    [self setNumberOfLines:0];
    //[self setLineBreakMode:UILineBreakModeWordWrap];
    
   // CGSize maximumLabelSize = CGSizeMake(self.frame.size.width,9999);
    
   // CGSize expectedLabelSize = [[self text] sizeWithFont:[self font]
     //                                  constrainedToSize:maximumLabelSize
       //                                    lineBreakMode:[self lineBreakMode]];
    
    
    CGSize expectedLabelSize = [[self text] boundingRectWithSize:CGSizeMake(151, 104) options:(NSStringDrawingUsesLineFragmentOrigin) attributes:@{NSFontAttributeName: self.font} context:nil].size;
    
    return expectedLabelSize.height;
}

@end
